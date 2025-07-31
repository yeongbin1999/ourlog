package com.back.ourlog.global.security.jwt;

public record TokenDto (
        String accessToken,
        String refreshToken
) {}

