package com.back.ourlog.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // 인증/인가 관련
    AUTH_INVALID_TOKEN("AUTH_001", "유효하지 않은 토큰입니다."),
    AUTH_EXPIRED_TOKEN("AUTH_002", "토큰이 만료되었습니다."),
    AUTH_UNAUTHORIZED("AUTH_003", "인증이 필요합니다."),
    AUTH_FORBIDDEN("AUTH_004", "접근 권한이 없습니다."),

    // 사용자 관련
    USER_NOT_FOUND("USER_001", "존재하지 않는 사용자입니다."),
    USER_DUPLICATE_EMAIL("USER_002", "이미 존재하는 이메일입니다."),
    USER_BANNED("USER_003", "차단된 사용자입니다."),

    // 로그인 관련
    LOGIN_FAILED("AUTH_005", "이메일 또는 비밀번호가 올바르지 않습니다."),

    // 다이어리 관련
    DIARY_NOT_FOUND("DIARY_001", "존재하지 않는 다이어리입니다."),

    // 콘텐츠 관련
    CONTENT_NOT_FOUND("CONTENT_001", "콘텐츠를 찾을 수 없습니다."),

    // 태그/장르/OTT 관련
    TAG_NOT_FOUND("TAG_001", "존재하지 않는 태그입니다."),
    GENRE_NOT_FOUND("GENRE_001", "존재하지 않는 장르입니다."),
    OTT_NOT_FOUND("OTT_001", "존재하지 않는 OTT입니다."),

    // 댓글 관련
    COMMENT_NOT_FOUND("COMMENT_001", "존재하지 않는 댓글입니다."),

    // 서버/시스템 관련 (HTTP 500)
    SERVER_ERROR("SERVER_500", "서버 내부 오류가 발생했습니다."),
    DATABASE_ERROR("SERVER_501", "데이터베이스 오류가 발생했습니다."),

    // 공통 에러 (HTTP 400~499 범위, 주로 프레임워크 레벨)
    BAD_REQUEST("COMMON_400", "잘못된 요청입니다."),
    FORBIDDEN("COMMON_403", "접근 권한이 없습니다."),
    NOT_FOUND("COMMON_404", "요청하신 리소스를 찾을 수 없습니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
