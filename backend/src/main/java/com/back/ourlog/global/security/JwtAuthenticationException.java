package com.back.ourlog.global.security;

import com.back.ourlog.global.exception.ErrorCode;
import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {
    private final ErrorCode errorCode;

    public JwtAuthenticationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

