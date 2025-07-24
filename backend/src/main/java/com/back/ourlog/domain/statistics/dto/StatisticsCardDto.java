package com.back.ourlog.domain.statistics.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsCardDto {
    private long totalDiaryCount;
    private double averageRating;
    private String favoriteGenre;
    private long favoriteGenreCount;
    private String favoriteEmotion;
    private long favoriteEmotionCount;
}
