package com.back.ourlog.global.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
public class RsData<T> {
    private final String resultCode;
    private final String msg;
    private final T data;

    // 성공
    public static <T> RsData<T> success(String msg, T data) {
        return new RsData<>("S-200", msg, data);
    }

    // 성공(데이터 없이)
    public static <T> RsData<T> success(String msg) {
        return new RsData<>("S-200", msg, null);
    }

    // 실패
    public static <T> RsData<T> fail(String resultCode, String msg) {
        return new RsData<>(resultCode, msg, null);
    }

    // 페이징 데이터 성공
    public static <T> RsData<PageResponse<T>> pagingSuccess(Page<T> page, String msg) {
        return new RsData<>("S-200", msg, PageResponse.from(page));
    }
}
