package com.back.ourlog.domain.diary.dto;

import java.util.List;
import com.back.ourlog.domain.content.entity.ContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DiaryWriteRequestDto(
        @NotBlank(message = "제목을 입력해주세요.")
        String title,

        @NotBlank(message = "내용을 입력해주세요.")
        String contentText,

        @NotNull Boolean isPublic,
        @NotNull Double rating,
        @NotBlank String externalId,
        @NotNull ContentType type,
        List<Integer> tagIds,
        List<Integer> genreIds,
        List<Integer> ottIds
) {

    // 테스트용 생성자
    public DiaryWriteRequestDto(String title, String contentText) {
        this(
                title,
                contentText,
                true,
                4.5,
                "external-id-test",
                ContentType.MOVIE,
                List.of(1, 2),
                List.of(1, 2),
                List.of(1, 2)
        );
    }

}
