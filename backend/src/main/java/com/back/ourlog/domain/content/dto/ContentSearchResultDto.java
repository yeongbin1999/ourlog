package com.back.ourlog.domain.content.dto;

import com.back.ourlog.domain.content.entity.ContentType;

import java.time.LocalDateTime;

public record ContentSearchResultDto(
        String externalId,
        String title,
        String creatorName,
        String description,
        String posterUrl,
        LocalDateTime releasedAt,
        ContentType type
) {}
