package com.back.ourlog.domain.diary.dto;

import com.back.ourlog.domain.diary.entity.Diary;

public record DiaryResponseDto(
        Integer id,
        Integer userId,
        Integer contentId,
        String title,
        String contentText,
        Float rating,
        Boolean isPublic,
        String createdAt,
        String modifiedAt
) {
    public static DiaryResponseDto from(Diary diary) {
        return new DiaryResponseDto(
                diary.getId(),
                diary.getUser() != null ? diary.getUser().getId() : null,
                diary.getContent() != null ? diary.getContent().getId() : null,
                diary.getTitle(),
                diary.getContentText(),
                diary.getRating(),
                diary.getIsPublic(),
                diary.getCreatedAt() != null ? diary.getCreatedAt().toString() : null,
                diary.getUpdatedAt() != null ? diary.getUpdatedAt().toString() : null
        );
    }
}