package com.back.ourlog.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 인증/인가 관련
    AUTH_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_001", "유효하지 않은 토큰입니다."),
    AUTH_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_002", "토큰이 만료되었습니다."),
    AUTH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_003", "인증이 필요합니다."),
    AUTH_FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH_004", "접근 권한이 없습니다."),

    // 사용자 관련
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "존재하지 않는 사용자입니다."),
    USER_DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USER_002", "이미 존재하는 이메일입니다."),
    USER_BANNED(HttpStatus.FORBIDDEN, "USER_003", "차단된 사용자입니다."),

    // 로그인 관련
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH_005", "이메일 또는 비밀번호가 올바르지 않습니다."),

    // 다이어리 관련
    DIARY_NOT_FOUND(HttpStatus.NOT_FOUND, "DIARY_001", "존재하지 않는 다이어리입니다."),

    // 콘텐츠 관련
    CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "CONTENT_001", "콘텐츠를 찾을 수 없습니다."),

    // 태그/장르/OTT 관련
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "TAG_001", "존재하지 않는 태그입니다."),
    GENRE_NOT_FOUND(HttpStatus.NOT_FOUND, "GENRE_001", "존재하지 않는 장르입니다."),
    OTT_NOT_FOUND(HttpStatus.NOT_FOUND, "OTT_001", "존재하지 않는 OTT입니다."),

    // 댓글 관련
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_001", "존재하지 않는 댓글입니다."),

    // 팔로우 관련
    FOLLOW_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "FOLLOW_001", "이미 팔로우한 사용자입니다."),
    FOLLOW_NOT_FOUND(HttpStatus.NOT_FOUND, "FOLLOW_002", "팔로우 관계가 존재하지 않습니다."),
    CANNOT_FOLLOW_SELF(HttpStatus.BAD_REQUEST, "FOLLOW_003", "자기 자신은 팔로우할 수 없습니다."),
    FOLLOW_ALREADY_REJECTED(HttpStatus.BAD_REQUEST, "FOLLOW_004", "이미 거절한 팔로우 요청입니다."),
    FOLLOW_ALREADY_ACCEPTED(HttpStatus.BAD_REQUEST, "FOLLOW_005", "이미 수락된 팔로우 요청입니다."),

    // 서버/시스템 관련
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_500", "서버 내부 오류가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_501", "데이터베이스 오류가 발생했습니다."),

    // 공통 에러
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_403", "접근 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_404", "요청하신 리소스를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}