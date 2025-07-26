package com.back.ourlog.domain.comment.controller;

import com.back.ourlog.domain.comment.dto.CommentRequestDto;
import com.back.ourlog.domain.comment.dto.CommentResponseDto;
import com.back.ourlog.domain.comment.entity.Comment;
import com.back.ourlog.domain.comment.service.CommentService;
import com.back.ourlog.domain.diary.entity.Diary;
import com.back.ourlog.domain.diary.service.DiaryService;
import com.back.ourlog.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@Log4j2
public class CommentController {
    private final CommentService commentService;
    private final DiaryService diaryService;
    @PostMapping
    public ResponseEntity<RsData<CommentResponseDto>> writeComment(@RequestBody CommentRequestDto req) {
        // 테스트 ver (우선 User가 있다고 가정)
        Comment comment = commentService.write(req.getDiaryId(),null, req.getContent());
        commentService.flush();

        CommentResponseDto res = new CommentResponseDto(comment);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RsData.of("201-1", "댓글이 등록되었습니다.", res));
    }

    @GetMapping("/{diaryId}")
    public ResponseEntity<RsData<List<CommentResponseDto>>> getComments(@PathVariable("diaryId") int diaryId) {
        Diary diary = diaryService.findById(diaryId).orElseThrow();
        List<Comment> comments = diary.getComments();

        List<CommentResponseDto> res = new ArrayList<>();
        for(Comment comment : comments) {
            res.add(new CommentResponseDto(comment));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(RsData.of("200-1", "%d번 다이어리 댓글이 조회되었습니다.".formatted(diaryId), res));
    }
}
