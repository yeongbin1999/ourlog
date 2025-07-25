package com.back.ourlog.global.rsData;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RsData<T> {
    private final String resultCode;
    private final String msg;
    private final T data;

}
