package com.back.ourlog.global.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {
    private final StringRedisTemplate redisTemplate;

    private String buildKey(String userId, String deviceId) {
        return "refreshToken:" + userId + ":" + deviceId;
    }

    public void save(String userId, String deviceId, String refreshToken, Duration expiration) {
        redisTemplate.opsForValue().set(buildKey(userId, deviceId), refreshToken, expiration);
    }

    public String find(String userId, String deviceId) {
        return redisTemplate.opsForValue().get(buildKey(userId, deviceId));
    }

    public void delete(String userId, String deviceId) {
        redisTemplate.delete(buildKey(userId, deviceId));
    }
}