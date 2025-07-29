package com.back.ourlog.external.library.dto;

import lombok.Data;

@Data
public class LibraryApiRequestDto {
    private String title;

    public LibraryApiRequestDto(String title) {
        this.title = title;
    }
}
