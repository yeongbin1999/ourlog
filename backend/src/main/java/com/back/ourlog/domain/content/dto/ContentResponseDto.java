package com.back.ourlog.domain.content.dto;

import com.back.ourlog.domain.content.entity.Content;
import com.back.ourlog.domain.content.entity.ContentType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContentResponseDto {
    private int id;
    private ContentType type;
    private String posterUrl;
    private String title;
    private String description;
    private LocalDateTime releasedAt;

    public ContentResponseDto(Content content) {
        id = content.getId();
        type = content.getType();
        posterUrl = content.getPosterUrl();
        title = content.getTitle();
        description = content.getDescription();
        releasedAt = content.getReleasedAt();
    }
}
