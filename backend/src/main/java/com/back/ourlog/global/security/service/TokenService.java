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
        if (!jwtProvider.validateToken(presentedRefreshToken)) {
            throw new CustomException(ErrorCode.AUTH_EXPIRED_TOKEN);
        }

        String userId = jwtProvider.getUserIdFromToken(presentedRefreshToken);

        // 새 토큰 발급 준비
        CustomUserDetails userDetails = customUserDetailsService.loadUserById(userId);
        String newAccessToken = jwtProvider.createAccessToken(userDetails);
        String newRefreshToken = jwtProvider.createRefreshToken(userDetails);

        // 원자적 토큰 회전 시도 (Lua 스크립트)
        Long result = refreshTokenRepository.rotateRefreshToken(userId, deviceId, presentedRefreshToken, newRefreshToken, jwtProvider.getRefreshTokenExpiration());

        if (result == null || result != 1L) {
            throw new CustomException(ErrorCode.AUTH_INVALID_TOKEN); // 토큰 재사용, 변조 등 실패
        }

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
