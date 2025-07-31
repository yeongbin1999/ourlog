package com.back.ourlog.domain.comment.service;

import com.back.ourlog.domain.comment.dto.CommentResponseDto;
import com.back.ourlog.domain.comment.entity.Comment;
import com.back.ourlog.domain.comment.repository.CommentRepository;
import com.back.ourlog.domain.diary.entity.Diary;
import com.back.ourlog.domain.diary.repository.DiaryRepository;
import com.back.ourlog.domain.user.entity.User;
import com.back.ourlog.global.exception.CustomException;
import com.back.ourlog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final DiaryRepository diaryRepository;
    private final CommentRepository commentRepository;
    @Transactional
    public CommentResponseDto write(int diaryId, User user, String content) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new CustomException(ErrorCode.DIARY_NOT_FOUND));
        Comment comment = diary.addComment(user, content);
        diaryRepository.flush();

        return new CommentResponseDto(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getComments(int diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new CustomException(ErrorCode.DIARY_NOT_FOUND));
        List<Comment> comments = diary.getComments();

        return comments.stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }

    public void update(int id, String content) {
        Comment comment = commentRepository.findById(id).orElseThrow();
        comment.update(content);
    }
}
