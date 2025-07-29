package com.back.ourlog.external.tmdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class TmdbMovieDto {

    private int id;

    private String title;

    @JsonProperty("overview")
    private String description;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("vote_average")
    private double voteAverage;

    // 기타 필요한 필드는 자유롭게 추가
}
