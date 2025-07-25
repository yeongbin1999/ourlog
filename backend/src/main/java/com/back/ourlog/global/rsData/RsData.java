package com.back.ourlog.global.rsData;

import com.back.ourlog.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RsData<T> {

    private String resultCode; // ErrorCode.code 또는 기존 코드 (200-0, 400-0 등)
    private String msg;        // 메시지
    private T data;            // 응답 데이터

    /**
     * 기존 방식 (레거시) - 코드, 메시지, 데이터 직접 지정
     */
    public static <T> RsData<T> of(String resultCode, String msg, T data) {
        return new RsData<>(resultCode, msg, data);
    }

    /**
     * 기존 방식 (레거시) - 성공 응답
     */
    public static <T> RsData<T> success(T data) {
        return new RsData<>("200-0", "성공", data);
    }

    /**
     * 기존 방식 (레거시) - 실패 응답
     */
    public static <T> RsData<T> fail(String msg) {
        return new RsData<>("400-0", msg, null);
    }

    /**
     * 신규 방식 - 성공
     */
    public static <T> RsData<T> success(String msg, T data) {
        return new RsData<>("SUCCESS_200", msg, data);
    }

    /**
     * 신규 방식 - ErrorCode 기반 실패 응답
     */
    public static <T> RsData<T> fail(ErrorCode errorCode) {
        return new RsData<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * 신규 방식 - ErrorCode 기반 실패 응답 + 커스텀 메시지 상세한 메시지를 주고 싶을 경우
     */
    public static <T> RsData<T> fail(ErrorCode errorCode, String customMsg) {
        return new RsData<>(errorCode.getCode(), customMsg, null);
    }

    /**
     * 성공 여부 (기존 2xx 방식과 신규 SUCCESS_200 모두 허용)
     */
    public boolean isSuccess() {
        return resultCode != null &&
                (resultCode.startsWith("2") || resultCode.startsWith("SUCCESS"));
    }

    public boolean isFail() {
        return !isSuccess();
    }
}
