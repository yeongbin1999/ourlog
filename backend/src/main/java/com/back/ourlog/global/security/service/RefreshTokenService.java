package com.back.ourlog.global.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final StringRedisTemplate redisTemplate;

    private String buildKey(String userId, String deviceId) {
        return "refreshToken:" + userId + ":" + deviceId;
    }

    public void saveRefreshToken(String userId, String deviceId, String refreshToken, Duration expiration) {
        redisTemplate.opsForValue().set(buildKey(userId, deviceId), refreshToken, expiration);
    }

    public String getRefreshToken(String userId, String deviceId) {
        return redisTemplate.opsForValue().get(buildKey(userId, deviceId));
    }

    public void deleteRefreshToken(String userId, String deviceId) {
        redisTemplate.delete(buildKey(userId, deviceId));
    }
}


