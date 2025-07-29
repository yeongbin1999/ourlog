package com.back.ourlog.external.library.dto;

import com.back.ourlog.domain.content.entity.ContentType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LibraryApiResponseDto {
    private String title;
    private ContentType contentType;

    private String creatorName;
    private String description;
    private String posterUrl;

    private LocalDateTime releasedAt;

    public LibraryApiResponseDto(String title, String creatorName, String description, String posterUrl, LocalDateTime releasedAt) {
        this.title = title;
        contentType = ContentType.BOOK;
        this.creatorName = creatorName;
        this.description = description;
        this.posterUrl = posterUrl;
        this.releasedAt = releasedAt;
    }
}
