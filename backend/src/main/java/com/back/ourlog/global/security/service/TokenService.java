package com.back.ourlog.global.security.service;

import com.back.ourlog.global.exception.CustomException;
import com.back.ourlog.global.exception.ErrorCode;
import com.back.ourlog.global.security.jwt.JwtProvider;
import com.back.ourlog.global.security.jwt.RefreshTokenRepository;
import com.back.ourlog.global.security.jwt.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService customUserDetailsService;

    /** ----------------------------------------------------------------
     * 1) 로그인 직후: Access / Refresh 토큰 모두 발급 & Redis 저장
     * ----------------------------------------------------------------*/
    public TokenDto issueTokens(CustomUserDetails userDetails, String deviceId) {
        String accessToken  = jwtProvider.createAccessToken(userDetails);
        String refreshToken = jwtProvider.createRefreshToken(userDetails);

        refreshTokenRepository.save(
                userDetails.getId().toString(),
                deviceId,
                refreshToken,
                jwtProvider.getRefreshTokenExpiration()
        );

        return new TokenDto(accessToken, refreshToken);
    }

    /** ----------------------------------------------------------------
     * 2) 리프레시 엔드포인트: 새 Access( + 새 Refresh) 발급
     * ----------------------------------------------------------------*/
    public TokenDto reIssueTokens(String presentedRefreshToken, String deviceId) {
        // 1) 리프레시 토큰 유효성 검사
        if (!jwtProvider.validateToken(presentedRefreshToken)) {
            throw new CustomException(ErrorCode.AUTH_EXPIRED_TOKEN);
        }

        // 2) 리프레시 토큰에서 userId 추출
        String userId = jwtProvider.getUserIdFromToken(presentedRefreshToken);

        // 3) Redis에 저장된 토큰과 일치 여부 확인
        String storedRefresh = refreshTokenRepository.find(userId, deviceId);
        if (storedRefresh == null || !storedRefresh.equals(presentedRefreshToken)) {
            throw new CustomException(ErrorCode.AUTH_INVALID_TOKEN);
        }

        // 4) userId로 CustomUserDetails 조회
        CustomUserDetails userDetails = customUserDetailsService.loadUserById(userId);

        // 5) 새 토큰 발급 (userDetails 기반)
        String newAccessToken  = jwtProvider.createAccessToken(userDetails);
        String newRefreshToken = jwtProvider.createRefreshToken(userDetails);

        // 6) Redis 갱신 (토큰 로테이션)
        refreshTokenRepository.save(
                userId,
                deviceId,
                newRefreshToken,
                jwtProvider.getRefreshTokenExpiration()
        );

        return new TokenDto(newAccessToken, newRefreshToken);
    }

    /** ----------------------------------------------------------------
     * 3) 로그아웃: Redis‑stored Refresh 토큰 삭제
     * ----------------------------------------------------------------*/
    public void logout(Integer userId, String deviceId) {
        refreshTokenRepository.delete(userId.toString(), deviceId);
        // 필요하면 Access 토큰 블랙리스트 처리 추가 가능
    }
}
