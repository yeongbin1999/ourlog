package com.back.ourlog.global.security.oauth;

import com.back.ourlog.domain.auth.dto.LoginResponse;
import com.back.ourlog.global.common.dto.RsData;
import com.back.ourlog.global.security.jwt.TokenDto;
import com.back.ourlog.global.security.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    @Value("${cookie.secure}")
    private boolean secure;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String deviceId = request.getHeader("X-Device-Id");

        TokenDto tokens = tokenService.issueTokens(oAuth2User, deviceId);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokens.refreshToken())
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(tokens.refreshTokenExpiration())
                .sameSite("Strict")
                .build();

        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        var loginResponse = new LoginResponse(tokens.accessToken());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        String json = objectMapper.writeValueAsString(RsData.success("OAuth 로그인 성공", loginResponse));
        response.getWriter().write(json);
    }
}