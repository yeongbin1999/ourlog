package com.back.ourlog.global.rsData;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RsData<T> {

    private String resultCode; // ErrorCode
    private String msg;        // 메시지
    private T data;            // 응답 데이터

    public static <T> RsData<T> of(String resultCode, String msg, T data) {
        return new RsData<>(resultCode, msg, data);
    }

    // 성공 응답
    public static <T> RsData<T> success(T data) {
        return new RsData<>("200-0", "성공", data);
    }

    // 실패 응답
    public static <T> RsData<T> fail(String msg) {
        return new RsData<>("400-0", msg, null);
    }

    // 성공 여부
    public boolean isSuccess() {
        return resultCode != null && resultCode.startsWith("2");
    }

    // 실패 여부
    public boolean isFail() {
        return !isSuccess();
    }
}
