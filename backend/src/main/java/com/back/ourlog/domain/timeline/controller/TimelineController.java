package com.back.ourlog.domain.timeline.controller;

import com.back.ourlog.domain.timeline.dto.TimelineResponse;
import com.back.ourlog.domain.timeline.service.TimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 프론트에서 이 API를 호출하면 공개된 일기 카드들을 받아올 수 있게 해줌..
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TimelineController {

    private final TimelineService timelineService;

    @GetMapping("/timeline")
    public ResponseEntity<List<TimelineResponse>> getPublicTimeline() {
        return ResponseEntity.ok(timelineService.getPublicTimeline());
    }
}