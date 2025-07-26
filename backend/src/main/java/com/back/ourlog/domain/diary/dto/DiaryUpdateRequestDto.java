package com.back.ourlog.domain.diary.dto;

import com.back.ourlog.domain.content.entity.ContentType;

import java.util.List;

public record DiaryUpdateRequestDto(
        String title,
        String contentText,
        Float rating,
        Boolean isPublic,
        String externalId,
        ContentType type,
        List<Integer> tagIds,
        List<Integer> genreIds,
        List<Integer> ottIds
) {}
