package com.back.ourlog.domain.comment.dto;

import com.back.ourlog.domain.comment.entity.Comment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponseDto {
    private int id;
    private String nickname;
    private String profileImageUrl;
    private String content;
    private LocalDateTime createdAt;

    // 테스트 ver (user가 있다고 가정)
    public CommentResponseDto(Comment comment) {
        id = comment.getId();
        nickname = null;
        profileImageUrl = null;
        content = comment.getContent();
        createdAt = comment.getCreatedAt();
    }
}
