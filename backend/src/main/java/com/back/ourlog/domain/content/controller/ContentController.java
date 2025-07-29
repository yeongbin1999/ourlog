package com.back.ourlog.domain.content.controller;

import com.back.ourlog.domain.content.dto.ContentResponseDto;
import com.back.ourlog.domain.content.dto.LibraryApiResponseDto;
import com.back.ourlog.domain.content.service.ContentService;
import com.back.ourlog.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/contents")
@RequiredArgsConstructor
@Log4j2
public class ContentController {
    private final ContentService contentService;

    @GetMapping("/{diaryId}")
    public ResponseEntity<RsData<ContentResponseDto>> getContent(@PathVariable("diaryId") int diaryId) {
        ContentResponseDto res = contentService.getContent(diaryId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(RsData.of("200-1", "%d번 다이어리의 조회 컨텐츠가 조회되었습니다.".formatted(diaryId), res));
    }

    @GetMapping("/library")
    public ResponseEntity<RsData<List<LibraryApiResponseDto>>> callLibraryApi() throws Exception {
        List<LibraryApiResponseDto> res = contentService.searchBooks("가");
        return ResponseEntity.status(HttpStatus.OK)
                .body(RsData.of("200-2", "도서관 자료가 조회되었습니다.", res));
    }
}