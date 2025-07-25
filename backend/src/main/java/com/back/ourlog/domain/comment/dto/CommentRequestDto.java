package com.back.ourlog.domain.comment.dto;

import lombok.Data;

@Data
public class CommentRequestDto {
    private int diaryId;
    private String content;
}
