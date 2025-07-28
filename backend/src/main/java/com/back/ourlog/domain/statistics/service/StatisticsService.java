package com.back.ourlog.domain.statistics.service;

import com.back.ourlog.domain.statistics.dto.*;
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

    public List<TypeCountDto> getTypeDistributionByUser(int userId) {
        return statisticsRepository.findTypeCountsByUserId(userId)
                .filter(list -> !list.isEmpty())  // 값 있으면 그대로 반환
                .orElseGet(() -> Collections.singletonList(new TypeCountDto("없음", 1L)));
    }
}
