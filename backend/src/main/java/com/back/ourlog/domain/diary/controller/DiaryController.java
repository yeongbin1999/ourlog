package com.back.ourlog.domain.diary.controller;

import com.back.ourlog.domain.diary.dto.DiaryDetailDto;
import com.back.ourlog.domain.diary.dto.DiaryResponseDto;
import com.back.ourlog.domain.diary.dto.DiaryWriteRequestDto;
import com.back.ourlog.domain.diary.entity.Diary;
import com.back.ourlog.domain.diary.service.DiaryService;
import com.back.ourlog.global.rsData.RsData;
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
@RequestMapping("/api/v1/diaries")
@Tag(name = "감상일기 API")
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping
    @Operation(summary = "감상일기 등록", description = "감상일기를 작성합니다.")
    public ResponseEntity<RsData<DiaryResponseDto>> writeDiary(
            @Valid @RequestBody DiaryWriteRequestDto req
    ) {
        Diary diary = diaryService.write(req, null); // 유저 인증 붙으면 'null' 대신 유저 넘기기

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RsData.of("201-1", "감상일기가 등록되었습니다.", DiaryResponseDto.from(diary)));

    }

    @GetMapping("/{diaryId}")
    public ResponseEntity<RsData<DiaryDetailDto>> getDiary(@PathVariable("diaryId") int diaryId) {
        Diary diary = diaryService.findById(diaryId).orElseThrow();
        List<String> TagNames = diaryService.getTagNames(diary);

        DiaryDetailDto diaryDetailDto = new DiaryDetailDto(diary, TagNames);

        return ResponseEntity.status(HttpStatus.OK)
                .body(RsData.of("200-1", "%d번 감상일기가 조회되었습니다.".formatted(diaryId), diaryDetailDto));
    }
}