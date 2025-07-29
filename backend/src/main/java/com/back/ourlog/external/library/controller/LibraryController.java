package com.back.ourlog.external.library.controller;

import com.back.ourlog.external.library.dto.LibraryApiResponseDto;
import com.back.ourlog.external.library.service.LibraryService;
import com.back.ourlog.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/library")
@RequiredArgsConstructor
public class LibraryController {
    private final LibraryService libraryService;
    @GetMapping()
    public ResponseEntity<RsData<List<LibraryApiResponseDto>>> getLibraryInfo(
            @RequestBody LibraryApiResponseDto libraryApiRequestDto) throws Exception {
        List<LibraryApiResponseDto> res = libraryService.searchBooks(libraryApiRequestDto.getTitle());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RsData.of("200-2", "도서 정보가 조회되었습니다.", res));
    }
}