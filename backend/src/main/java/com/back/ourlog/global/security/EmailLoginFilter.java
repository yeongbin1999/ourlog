package com.back.ourlog.global.security;

import com.back.ourlog.global.exception.ErrorCode;
import com.back.ourlog.global.rsData.RsData;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;
import java.util.Map;

public class EmailLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final JwtProvider jwtProvider;

    public EmailLoginFilter(String defaultFilterProcessesUrl,
                            AuthenticationManager authenticationManager,
                            JwtProvider jwtProvider) {
        super(defaultFilterProcessesUrl);
        setAuthenticationManager(authenticationManager);
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(email, password);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {

        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
        String accessToken = jwtProvider.createAccessToken(userDetails);
        String refreshToken = jwtProvider.createRefreshToken(userDetails);

        // 1. Access Token → 헤더
        response.setHeader("Authorization", "Bearer " + accessToken);

        // 2. Refresh Token → HttpOnly 쿠키
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true); // 배포 시 true
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge((int) jwtProvider.getRefreshTokenExpiration().getSeconds());
        // SameSite 설정은 수동으로 Response Header에 추가할 수 있음

        response.addCookie(refreshCookie);

        // 3. 응답 바디 (RsData 사용)
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");

        RsData<Map<String, String>> body = RsData.success(
                Map.of("accessToken", accessToken)
        );

        new ObjectMapper().writeValue(response.getWriter(), body);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        RsData<?> errorBody = RsData.fail(ErrorCode.LOGIN_FAILED);

        new ObjectMapper().writeValue(response.getWriter(), errorBody);
    }
}
