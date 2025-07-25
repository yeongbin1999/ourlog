package com.back.ourlog.domain.statistics.controller;

import com.back.ourlog.domain.statistics.dto.StatisticsCardDto;
import com.back.ourlog.domain.statistics.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/statistics")
public class ApiV1StatisticsController {

    private final StatisticsService statisticsService;


    @GetMapping(value = "/card")
    @Operation(summary = "통계 카드 조회", description = "총 감상 수, 평균 별정, 선호 장르, 주요 감정을 조회합니다.")
    public StatisticsCardDto getStatisticsCard() {
        int userId = 1; // 임시 값, 실제로는 인증된 사용자 ID를 사용해야 합니다.
        return statisticsService.getStatisticsCardByUserId(userId);
    }
}
