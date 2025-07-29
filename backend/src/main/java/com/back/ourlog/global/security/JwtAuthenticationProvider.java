package com.back.ourlog.global.security;

import com.back.ourlog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();

        if (!jwtTokenProvider.validateToken(token)) {
            throw new JwtAuthenticationException(ErrorCode.AUTH_INVALID_TOKEN);
        }

        String userId = jwtTokenProvider.getUserIdFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

        return new JwtAuthenticationToken(userDetails, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
