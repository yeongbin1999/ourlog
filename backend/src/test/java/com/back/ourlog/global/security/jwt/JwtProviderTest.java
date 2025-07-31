package com.back.ourlog.global.security.jwt;

import com.back.ourlog.domain.user.entity.Role;
import com.back.ourlog.domain.user.entity.User;
import com.back.ourlog.global.exception.ErrorCode;
import com.back.ourlog.global.security.exception.JwtAuthenticationException;
import com.back.ourlog.global.security.service.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

    private static final String SECRET_KEY = "verysecretkeyverysecretkeyverysecretkey12"; // 32바이트 이상
    private static final Duration ACCESS_TOKEN_EXPIRATION = Duration.ofMinutes(30);
    private static final Duration REFRESH_TOKEN_EXPIRATION = Duration.ofDays(7);

    private JwtProvider jwtProvider;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() throws Exception {
        jwtProvider = new JwtProvider(SECRET_KEY, ACCESS_TOKEN_EXPIRATION, REFRESH_TOKEN_EXPIRATION);

        // User 객체 생성 (id가 null일 수 있으므로 reflection으로 강제 설정)
        User user = new User("test@example.com", "1112", "testnick", Role.USER.name(), "hi");

        // id 필드에 접근하여 1 세팅 (테스트용)
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, 1);

        userDetails = new CustomUserDetails(user);
    }

    @Test
    @DisplayName("AccessToken 생성 및 유효성 검증")
    void createAccessToken_ShouldReturnValidToken() {
        // given
        // when
        String accessToken = jwtProvider.createAccessToken(userDetails);

        // then
        assertNotNull(accessToken);
        assertTrue(jwtProvider.validateToken(accessToken));
    }

    @Test
    @DisplayName("RefreshToken 생성 및 유효성 검증")
    void createRefreshToken_ShouldReturnValidToken() {
        // given
        // when
        String refreshToken = jwtProvider.createRefreshToken(userDetails);

        // then
        assertNotNull(refreshToken);
        assertTrue(jwtProvider.validateToken(refreshToken));
    }

    @Test
    @DisplayName("AccessToken 내 클레임 포함 확인")
    void shouldIncludeClaimsInAccessToken() {
        String token = jwtProvider.createAccessToken(userDetails);
        Claims claims = jwtProvider.parseClaims(token);
        assertEquals(userDetails.getNickname(), claims.get("nickname"));
        assertEquals(userDetails.getRole().name(), claims.get("role"));
    }

    @Test
    @DisplayName("토큰에서 UserId 추출")
    void getUserIdFromToken_ShouldReturnCorrectUserId() {
        // given
        String accessToken = jwtProvider.createAccessToken(userDetails);

        // when
        String extractedUserId = jwtProvider.getUserIdFromToken(accessToken);

        // then
        assertEquals(userDetails.getId().toString(), extractedUserId);
    }

    @Test
    @DisplayName("유효하지 않은 토큰은 validateToken()에서 false 반환")
    void validateToken_ShouldReturnFalseForInvalidToken() {
        // given
        String invalidToken = "this.is.not.a.valid.token";

        // when, then
        assertFalse(jwtProvider.validateToken(invalidToken));
    }

    @Test
    @DisplayName("만료된 토큰은 parseClaims() 호출 시 JwtAuthenticationException 발생")
    void parseClaims_ShouldThrowExceptionForExpiredToken() {
        // given
        Date pastIssuedAt = new Date(System.currentTimeMillis() - 10000000);
        Date pastExpiration = new Date(System.currentTimeMillis() - 5000);

        String expiredToken = Jwts.builder()
                .setSubject(userDetails.getId().toString())
                .setIssuedAt(pastIssuedAt)
                .setExpiration(pastExpiration)
                .signWith(jwtProvider.getKey(), SignatureAlgorithm.HS256)
                .compact();

        // when, then
        JwtAuthenticationException exception = assertThrows(JwtAuthenticationException.class,
                () -> jwtProvider.parseClaims(expiredToken));

        assertEquals(ErrorCode.AUTH_EXPIRED_TOKEN, exception.getErrorCode());
    }
}
