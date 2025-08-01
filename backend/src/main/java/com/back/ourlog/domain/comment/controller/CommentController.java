package com.back.ourlog.domain.comment.controller;

import com.back.ourlog.domain.comment.dto.CommentRequestDto;
import com.back.ourlog.domain.comment.dto.CommentResponseDto;
import com.back.ourlog.domain.comment.dto.CommentUpdateRequestDto;
import com.back.ourlog.domain.comment.service.CommentService;
import com.back.ourlog.global.common.dto.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@Tag(name = "댓글 API")
public class CommentController {
    private final CommentService commentService;
    @PostMapping
    @Operation(summary = "댓글 등록")
    public ResponseEntity<RsData<CommentResponseDto>> writeComment(@RequestBody @Valid CommentRequestDto req) {
        // 테스트 ver (우선 User가 있다고 가정)
        CommentResponseDto res = commentService.write(req.getDiaryId(),null, req.getContent());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RsData.of("201-1", "댓글이 등록되었습니다.", res));
    }

    @GetMapping("/{diaryId}")
    @Operation(summary = "댓글 조회")
    public ResponseEntity<RsData<List<CommentResponseDto>>> getComments(@PathVariable("diaryId") int diaryId) {
        List<CommentResponseDto> res = commentService.getComments(diaryId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(RsData.of("200-1", "%d번 다이어리 댓글이 조회되었습니다.".formatted(diaryId), res));
    }

    @PutMapping()
    @Operation(summary = "댓글 수정")
    public ResponseEntity<RsData<Void>> updateComment
            (@RequestBody CommentUpdateRequestDto req) {
        commentService.update(req.getId(), req.getContent());

        return ResponseEntity.status(HttpStatus.OK)
                .body(RsData.of("200-0"
                        ,"%d번 댓글이 수정되었습니다.".formatted(req.getId())
                        , null));
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제")
    public ResponseEntity<RsData<Void>> deleteComment(@PathVariable("commentId") int commentId) {
        commentService.delete(commentId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(RsData.of("200-0"
                        ,"%d번 댓글이 삭제되었습니다.".formatted(commentId)
                        , null));
    }
}
