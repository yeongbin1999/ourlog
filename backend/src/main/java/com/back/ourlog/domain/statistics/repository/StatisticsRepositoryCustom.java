package com.back.ourlog.domain.statistics.repository;

import com.back.ourlog.domain.statistics.dto.*;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsRepositoryCustom {

    /** 타입 그래프 데이터 조회 */
    List<TypeLineGraphDto> findTypeLineMonthly(Integer userId, LocalDateTime start, LocalDateTime end);
    List<TypeLineGraphDto> findTypeLineDaily(Integer userId, LocalDateTime start, LocalDateTime end);
    List<TypeRankDto> findTypeRanking(Integer userId, LocalDateTime start, LocalDateTime end);

    /** 장르 그래프 데이터 조회 */
    List<GenreLineGraphDto> findGenreLineMonthly(Integer userId, LocalDateTime start, LocalDateTime end);
    List<GenreLineGraphDto> findGenreLineDaily(Integer userId, LocalDateTime start, LocalDateTime end);
    List<GenreRankDto> findGenreRanking(Integer userId, LocalDateTime start, LocalDateTime end);

    /** 감정 그래프 데이터 조회 */
    List<EmotionLineGraphDto> findEmotionLineMonthly(Integer userId, LocalDateTime start, LocalDateTime end);
    List<EmotionLineGraphDto> findEmotionLineDaily(Integer userId, LocalDateTime start, LocalDateTime end);
    List<EmotionRankDto> findEmotionRanking(Integer userId, LocalDateTime start, LocalDateTime end);
}
