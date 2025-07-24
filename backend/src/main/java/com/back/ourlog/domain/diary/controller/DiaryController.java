package com.back.ourlog.domain.diary.controller;

import com.back.ourlog.domain.diary.dto.DiaryResponseDto;
import com.back.ourlog.domain.diary.dto.DiaryWriteRequestDto;
import com.back.ourlog.domain.diary.entity.Diary;
import com.back.ourlog.domain.diary.service.DiaryService;
import com.back.ourlog.global.rsData.RsData;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "감상일기 API")
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping("/api/v1/diaries")
    public ResponseEntity<RsData<DiaryResponseDto>> writeDiary(
            @Valid @RequestBody DiaryWriteRequestDto req
    ) {
        Diary diary = diaryService.write(req, null); // 유저 인증 붙으면 'null' 대신 유저 넘기기

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new RsData<>(
                        "201-1",
                        "감상일기가 등록되었습니다.",
                        DiaryResponseDto.from(diary)
                )
        );



    }

}
