package com.back.ourlog.domain.like.controller;

import com.back.ourlog.domain.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor

// 좋아요 등록/취소 API 제공..
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{diaryId}")  // 좋아요 등록..
    public ResponseEntity<Void> like(@PathVariable Integer diaryId) {
        likeService.like(diaryId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{diaryId}")    // 좋아요 취소..
    public ResponseEntity<Void> unlike(@PathVariable Integer diaryId) {
        likeService.unlike(diaryId);
        return ResponseEntity.ok().build();
    }
}
