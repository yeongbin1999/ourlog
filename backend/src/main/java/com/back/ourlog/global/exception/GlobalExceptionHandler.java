package com.back.ourlog.global.exception;

import com.back.ourlog.global.rsData.RsData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // CustomException 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<RsData<Void>> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        String message = ex.getMessage() != null ? ex.getMessage() : errorCode.getMessage();

        return ResponseEntity
                .status(resolveHttpStatus(errorCode))
                .body(RsData.fail(errorCode, message));
    }

    // IllegalArgumentException → BAD_REQUEST로 통일
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RsData<Void>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity
                .badRequest()
                .body(RsData.fail(ErrorCode.BAD_REQUEST, e.getMessage()));
    }

    // @Valid 실패 → BAD_REQUEST
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RsData<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldError() != null ?
                e.getBindingResult().getFieldError().getDefaultMessage() :
                ErrorCode.BAD_REQUEST.getMessage();

        return ResponseEntity
                .badRequest()
                .body(RsData.fail(ErrorCode.BAD_REQUEST, errorMessage));
    }

    // 예상 못한 예외 → SERVER_ERROR
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RsData<Void>> handleException(Exception e) {
        e.printStackTrace(); // 로그 남기기
        return ResponseEntity
                .internalServerError()
                .body(RsData.fail(ErrorCode.SERVER_ERROR, "서버 오류: " + e.getMessage()));
    }

    /**
     * ErrorCode prefix 기반 HTTP 상태 결정
     */
    private HttpStatus resolveHttpStatus(ErrorCode errorCode) {
        String code = errorCode.getCode();

        if (code.startsWith("AUTH_")) {
            if (code.equals("AUTH_004")) return HttpStatus.FORBIDDEN;
            return HttpStatus.UNAUTHORIZED;
        }

        if (code.startsWith("USER_")) {
            if (code.equals("USER_002")) return HttpStatus.CONFLICT;
            return HttpStatus.NOT_FOUND;
        }

<<<<<<< HEAD
        if (code.startsWith("DIARY_")) return HttpStatus.NOT_FOUND;

=======
>>>>>>> 864ac82 (Remove: 기존 GlobalException 삭제)
        if (code.startsWith("COMMON_400")) return HttpStatus.BAD_REQUEST;
        if (code.startsWith("COMMON_403")) return HttpStatus.FORBIDDEN;
        if (code.startsWith("COMMON_404")) return HttpStatus.NOT_FOUND;

        if (code.startsWith("SERVER_")) return HttpStatus.INTERNAL_SERVER_ERROR;

        return HttpStatus.BAD_REQUEST;
    }
<<<<<<< HEAD

}
=======
}

>>>>>>> 864ac82 (Remove: 기존 GlobalException 삭제)
