package com.back.ourlog.domain.statistics.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsCardDto {
    private long totalDiaryCount;
    private double averageRating;
    private String favoriteType;
    private long favoriteTypeCount;
    private String favoriteEmotion;
    private long favoriteEmotionCount;
}
