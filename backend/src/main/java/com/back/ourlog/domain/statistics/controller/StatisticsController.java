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

    int userId = 1; // 임시 값, 실제로는 인증된 사용자 ID를 사용해야 합니다.

    @GetMapping(value = "/card")
    @Operation(summary = "통계 카드 조회", description = "총 감상 수, 평균 별정, 선호 장르, 주요 감정을 조회합니다.")
    public ResponseEntity<StatisticsCardDto> getStatisticsCard() {
        return ResponseEntity.ok(statisticsService.getStatisticsCardByUserId(userId));
    }

    @GetMapping(value = "/monthly-diary-graph")
    @Operation(summary = "최근 6개월 월 별 감상 수", description = "특정 회원의 최근 6개월 월 별 감상 수를 조회합니다")
    public ResponseEntity<List<MonthlyDiaryCount>> getLast6MonthsDiaryCounts() {
        return ResponseEntity.ok(statisticsService.getLast6MonthsDiaryCountsByUser(userId));
    }

    @GetMapping(value = "/type-distribution")
    @Operation(summary = "콘테츠 타입 분포", description = "특정 회원의 콘테츠 타입 분포를 조회합니다")
    public ResponseEntity<List<TypeCountDto>> getTypeDistribution() {
        return ResponseEntity.ok(statisticsService.getTypeDistributionByUser(userId));
    }

    @GetMapping("/type-graph")
    @Operation(summary = "콘텐츠 타입 그래프", description = "콘텐츠 타입에 대한 그래프 데이터를 조회합니다.")
    public ResponseEntity<TypeGraphResponse> getTypeGraph(@RequestParam PeriodOption period) {
        TypeGraphRequest req = new TypeGraphRequest(userId, period);
        return ResponseEntity.ok(statisticsService.getTypeGraph(req));
    }

    @GetMapping("/genre-graph")
    @Operation(summary = "장르 그래프", description = "장르에 대한 그래프 데이터를 조회합니다.")
    public ResponseEntity<GenreGraphResponse> getGenreGraph(@RequestParam PeriodOption period) {
        return ResponseEntity.ok(statisticsService.getGenreGraph(userId, period));
    }

    @GetMapping("/emotion-graph")
    @Operation(summary = "감정 그래프", description = "감정에 대한 그래프 데이터를 조회합니다.")
    public ResponseEntity<EmotionGraphResponse> getEmotionGraph(@RequestParam PeriodOption period) {
        EmotionGraphResponse res = statisticsService.getEmotionGraph(userId, period);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/ott-graph")
    @Operation(summary = "OTT 그래프", description = "OTT에 대한 그래프 데이터를 조회합니다.")
    public ResponseEntity<OttGraphResponse> getOttGraph(@RequestParam PeriodOption period) {
        OttGraphResponse res = statisticsService.getOttGraph(userId, period);
        return ResponseEntity.ok(res);
    }
}
