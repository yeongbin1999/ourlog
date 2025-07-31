package com.back.ourlog.domain.statistics.service;

import com.back.ourlog.domain.content.entity.ContentType;
import com.back.ourlog.domain.statistics.dto.*;
import com.back.ourlog.domain.statistics.enums.PeriodOption;
import com.back.ourlog.domain.statistics.repository.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;

    /** 통계 카드 조회 */
    @Transactional(readOnly = true)
    public StatisticsCardDto getStatisticsCardByUserId(int userId) {
        FavoriteTypeAndCountDto favoriteGenre = getFavoriteGenreAndCount(userId);
        FavoriteEmotionAndCountDto favoriteEmotion = getFavoriteEmotionAndCount(userId);

        return StatisticsCardDto.builder()
                .totalDiaryCount(getTotalDiaryCount(userId))
                .averageRating(getAverageRating(userId))
                .favoriteType(favoriteGenre.getFavoriteType())
                .favoriteTypeCount(favoriteGenre.getFavoriteTypeCount())
                .favoriteEmotion(favoriteEmotion.getFavoriteEmotion())
                .favoriteEmotionCount(favoriteEmotion.getFavoriteEmotionCount())
                .build();
    }

    /** 총 다이어리 개수 */
    private long getTotalDiaryCount(int userId) {
        return statisticsRepository.getTotalDiaryCountByUserId(userId);
    }

    /** 평균 평점 (없으면 0.0) */
    private double getAverageRating(int userId) {
        return statisticsRepository.getAverageRatingByUserId(userId)
                .orElse(0.0);
    }

    /** 좋아하는 타입 및 개수 (없으면 new(없음, 0L)) */
    private FavoriteTypeAndCountDto getFavoriteGenreAndCount(int userId) {
        return statisticsRepository.findFavoriteTypeAndCountByUserId(userId)
                .orElse(new FavoriteTypeAndCountDto("없음", 0L));
    }

    /** 좋아하는 감정(Tag) 및 개수 (없으면 new(없음, 0L)) */
    private FavoriteEmotionAndCountDto getFavoriteEmotionAndCount(int userId) {
        return statisticsRepository.findFavoriteEmotionAndCountByUserId(userId)
                .orElse(new FavoriteEmotionAndCountDto("없음", 0L));
    }

    /** 특정 회원의 최근 6개월 월 별 감상 수 조회 */
    @Transactional(readOnly = true)
    public List<MonthlyDiaryCount> getLast6MonthsDiaryCountsByUser(Integer userId) {
        // 6개월 전 첫날
        LocalDate now = LocalDate.now();
        LocalDate startMonth = now.minusMonths(5).withDayOfMonth(1);

        // DB조회: 결과는 작성된 달에만 존재
        List<MonthlyDiaryCount> counts = statisticsRepository.countMonthlyDiaryByUserId(userId, startMonth.atStartOfDay());

        // Map으로 매핑 (period -> views)
        Map<String, Long> countMap = counts.stream()
                .collect(Collectors.toMap(MonthlyDiaryCount::getPeriod, MonthlyDiaryCount::getViews));

        List<MonthlyDiaryCount> result = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            LocalDate month = startMonth.plusMonths(i);
            String period = month.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            Long views = countMap.getOrDefault(period, 0L);
            result.add(new MonthlyDiaryCount(period, views));
        }
        return result;
    }

    /** 특정 회원의 콘텐츠 타입 분포 조회 */
    @Transactional(readOnly = true)
    public List<TypeCountDto> getTypeDistributionByUser(int userId) {
        return statisticsRepository.findTypeCountsByUserId(userId)
                .filter(list -> !list.isEmpty())  // 값 있으면 그대로 반환
                .orElseGet(() -> Collections.singletonList(new TypeCountDto("없음", 1L)));
    }

    /** 특정 회원의 콘텐츠 타입 그래프 조회 */
    @Transactional(readOnly = true)
    public TypeGraphResponse getTypeGraph(TypeGraphRequest req) {
        LocalDateTime now   = LocalDateTime.now();
        LocalDateTime start = calculateStart(req.getPeriod(), now);
        LocalDateTime end   = now.plusDays(1);

        List<TypeRankDto> ranking = statisticsRepository.findTypeRanking(req.getUserId(), start, end);

        List<TypeLineGraphDto> trend;
        switch (req.getPeriod()) {
            case LAST_MONTH, LAST_WEEK ->
                    trend = statisticsRepository.findTypeLineDaily(req.getUserId(), start, end);
            default ->
                    trend = statisticsRepository.findTypeLineMonthly(req.getUserId(), start, end);
        }

        return new TypeGraphResponse(trend, ranking);
    }

    /** 특정 회원의 장르 타입 그래프 조회 */
    @Transactional(readOnly = true)
    public GenreGraphResponse getGenreGraph(int userId, PeriodOption period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = calculateStart(period, now);
        LocalDateTime end = now.plusDays(1);

        List<GenreLineGraphDto> graph;
        switch (period) {
            case LAST_MONTH, LAST_WEEK ->
                    graph = statisticsRepository.findGenreLineDaily(userId, start, end);
            default ->
                    graph = statisticsRepository.findGenreLineMonthly(userId, start, end);
        }

        List<GenreRankDto> ranking = statisticsRepository.findGenreRanking(userId, start, end);

        return new GenreGraphResponse(graph, ranking);
    }

    public EmotionGraphResponse getEmotionGraph(int userId, PeriodOption period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = calculateStart(period, now);
        LocalDateTime end = now.plusDays(1);

        List<EmotionLineGraphDto> line;
        switch (period) {
            case LAST_MONTH, LAST_WEEK ->
                    line = statisticsRepository.findEmotionLineDaily(userId, start, end);
            default ->
                    line = statisticsRepository.findEmotionLineMonthly(userId, start, end);
        }

        List<EmotionRankDto> ranking = statisticsRepository.findEmotionRanking(userId, start, end);

        return new EmotionGraphResponse(line, ranking);
    }

    private LocalDateTime calculateStart(PeriodOption period, LocalDateTime now) {
        return switch (period) {
            case THIS_YEAR    -> now.withDayOfYear(1);
            case LAST_6_MONTHS -> now.minusMonths(5).withDayOfMonth(1);
            case LAST_MONTH   -> now.minusMonths(1).withDayOfMonth(1);
            case LAST_WEEK    -> now.minusWeeks(1);
            default           -> LocalDateTime.of(1970,1,1,0,0);
        };
    }

}
