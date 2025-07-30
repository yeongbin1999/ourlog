package com.back.ourlog.domain.statistics.controller;

import com.back.ourlog.domain.statistics.dto.*;
import com.back.ourlog.domain.statistics.enums.PeriodOption;
import com.back.ourlog.domain.statistics.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;


    @GetMapping(value = "/card")
    @Operation(summary = "통계 카드 조회", description = "총 감상 수, 평균 별정, 선호 장르, 주요 감정을 조회합니다.")
    public StatisticsCardDto getStatisticsCard() {
        int userId = 1; // 임시 값, 실제로는 인증된 사용자 ID를 사용해야 합니다.
        return statisticsService.getStatisticsCardByUserId(userId);
    }

    @GetMapping(value = "/monthly-diary-graph")
    @Operation(summary = "최근 6개월 월 별 감상 수", description = "특정 회원의 최근 6개월 월 별 감상 수를 조회합니다")
    public List<MonthlyDiaryCount> getLast6MonthsDiaryCounts() {
        int userId = 1; // 임시 값, 실제로는 인증된 사용자 ID를 사용해야 합니다.
        return statisticsService.getLast6MonthsDiaryCountsByUser(userId);
    }

    @GetMapping(value = "/type-distribution")
    @Operation(summary = "콘테츠 타입 분포", description = "특정 회원의 콘테츠 타입 분포를 조회합니다")
    public List<TypeCountDto> getTypeDistribution() {
        int userId = 1; // 임시 값, 실제로는 인증된 사용자 ID를 사용해야 합니다.
        return statisticsService.getTypeDistributionByUser(userId);
    }

    @GetMapping("/type-graph")
    public TypeGraphResponse getTypeGraph(@RequestParam PeriodOption period) {
        int userId = 1; // 임시 값, 실제로는 인증된 사용자 ID를 사용해야 합니다.
        TypeGraphRequest req = new TypeGraphRequest(userId, period);
        return statisticsService.getTypeGraph(req);
    }

    @GetMapping("/genre-graph")
    public ResponseEntity<GenreGraphResponse> getGenreGraph(@RequestParam PeriodOption period) {
        int userId = 1;
        return ResponseEntity.ok(statisticsService.getGenreGraph(userId, period));
    }
}
