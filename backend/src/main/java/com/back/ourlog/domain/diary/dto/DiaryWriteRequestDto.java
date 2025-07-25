package com.back.ourlog.domain.diary.dto;

import java.util.List;
import com.back.ourlog.domain.content.entity.ContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DiaryWriteRequestDto(
        @NotBlank String title,
        @NotBlank String contentText,
        @NotNull Boolean isPublic,
        @NotNull Double rating,
        @NotBlank String externalId,
        @NotNull ContentType type,
        List<Integer> tagIds,
        List<Integer> genreIds,
        List<Integer> ottIds
) {}