package com.back.ourlog.domain.content.dto;

import com.back.ourlog.domain.content.entity.ContentType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ContentSearchResultDto(
        String externalId,
        String title,
        String creatorName,
        String description,
        String posterUrl,
        LocalDateTime releasedAt,
        ContentType type,
        List<String> genres
) {}
