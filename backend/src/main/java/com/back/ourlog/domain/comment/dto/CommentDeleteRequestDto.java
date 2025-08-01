package com.back.ourlog.domain.comment.dto;

import lombok.Data;

@Data
public class CommentDeleteRequestDto {
    private int id;

    public CommentDeleteRequestDto(int id) {
        this.id = id;
    }
}
