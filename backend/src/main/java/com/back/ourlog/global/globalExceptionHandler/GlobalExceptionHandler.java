package com.back.ourlog.global.globalExceptionHandler;

import com.back.ourlog.global.rsData.RsData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RsData<Void>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity
                .badRequest()
                .body(RsData.fail(e.getMessage()));
    }

    // @Valid 유효성 검사 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RsData<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldError() != null ?
                e.getBindingResult().getFieldError().getDefaultMessage() :
                "입력값이 올바르지 않습니다.";
        return ResponseEntity
                .badRequest()
                .body(RsData.fail(errorMessage));
    }

    // 기타 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RsData<Void>> handleException(Exception e) {
        return ResponseEntity
                .internalServerError()
                .body(RsData.fail("서버 오류: " + e.getMessage()));
    }

    private int getHttpStatusFromCode(String code) {
        if (code.startsWith("401")) return 401;
        if (code.startsWith("404")) return 404;
        if (code.startsWith("409")) return 409;
        return 400; // 기본값
    }

}
