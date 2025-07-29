package com.back.ourlog.domain.content.dto;

import lombok.Data;

@Data
public class LibraryApiRequestDto {
    private String title;

    public LibraryApiRequestDto(String title) {
        this.title = title;
    }
}
