package com.back.ourlog.domain.statistics.repository;

import com.back.ourlog.domain.statistics.dto.TypeLineGraphDto;
import com.back.ourlog.domain.statistics.dto.TypeRankDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsRepositoryCustom {
    List<TypeLineGraphDto> findTypeLineMonthly(Integer userId, LocalDateTime start, LocalDateTime end);

    List<TypeLineGraphDto> findTypeLineDaily(Integer userId, LocalDateTime start, LocalDateTime end);

    List<TypeRankDto> findTypeRanking(Integer userId, LocalDateTime start, LocalDateTime end);
}
