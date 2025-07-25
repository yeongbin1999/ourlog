package com.back.ourlog.domain.comment.service;

import com.back.ourlog.domain.comment.entity.Comment;
import com.back.ourlog.domain.comment.repository.CommentRepository;
import com.back.ourlog.domain.diary.entity.Diary;
import com.back.ourlog.domain.diary.repository.DiaryRepository;
import com.back.ourlog.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final DiaryRepository diaryRepository;
    @Transactional
    public Comment write(int diaryId, User user, String content) {
        Diary diary = diaryRepository.findById(diaryId).orElseThrow();
        // 테스트 ver (원래는 user가 있다고 가정)
        Comment comment = new Comment(diary,null, content);

        return commentRepository.save(comment);
    }
}
