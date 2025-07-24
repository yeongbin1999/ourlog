package com.back.ourlog.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FavoriteGenreAndCountDto {
    private String favoriteGenre;
    private long favoriteGenreCount;
}
