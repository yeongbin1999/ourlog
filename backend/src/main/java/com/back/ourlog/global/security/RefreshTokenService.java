package com.back.ourlog.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final StringRedisTemplate redisTemplate;

    private String buildKey(String userId, String deviceInfo) {
        return "refreshToken:" + userId + ":" + deviceInfo;
    }

    public void saveRefreshToken(String userId, String deviceInfo, String refreshToken, Duration expiration) {
        redisTemplate.opsForValue().set(buildKey(userId, deviceInfo), refreshToken, expiration);
    }

    public String getRefreshToken(String userId, String deviceInfo) {
        return redisTemplate.opsForValue().get(buildKey(userId, deviceInfo));
    }

    public void deleteRefreshToken(String userId, String deviceInfo) {
        redisTemplate.delete(buildKey(userId, deviceInfo));
    }
}

