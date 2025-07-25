package com.back.ourlog.domain.statistics.service;

import com.back.ourlog.domain.statistics.dto.FavoriteEmotionAndCountDto;
import com.back.ourlog.domain.statistics.dto.FavoriteTypeAndCountDto;
import com.back.ourlog.domain.statistics.dto.StatisticsCardDto;
import com.back.ourlog.domain.statistics.repository.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;

    /** 통계 카드 조회 */
    @Transactional(readOnly = true)
    public StatisticsCardDto getStatisticsCardByUserId(int userId) {
        StatisticsCardDto dto = new StatisticsCardDto();

        dto.setTotalDiaryCount(getTotalDiaryCount(userId));

        dto.setAverageRating(getAverageRating(userId));

        FavoriteTypeAndCountDto favoriteGenre = getFavoriteGenreAndCount(userId);
        dto.setFavoriteType(favoriteGenre.getFavoriteType());
        dto.setFavoriteTypeCount(favoriteGenre.getFavoriteTypeCount());

        FavoriteEmotionAndCountDto favoriteEmotion = getFavoriteEmotionAndCount(userId);
        dto.setFavoriteEmotion(favoriteEmotion.getFavoriteEmotion());
        dto.setFavoriteEmotionCount(favoriteEmotion.getFavoriteEmotionCount());

        return dto;
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
}
