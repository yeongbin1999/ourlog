package com.back.ourlog.external.tmdb.service;

import com.back.ourlog.domain.content.dto.ContentSearchResultDto;
import com.back.ourlog.domain.content.entity.ContentType;
import com.back.ourlog.external.tmdb.client.TmdbClient;
import com.back.ourlog.external.tmdb.dto.TmdbCreditsResponse;
import com.back.ourlog.external.tmdb.dto.TmdbCrewDto;
import com.back.ourlog.external.tmdb.dto.TmdbMovieDto;
import com.back.ourlog.external.tmdb.dto.TmdbSearchResponse;
import com.back.ourlog.global.exception.CustomException;
import com.back.ourlog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TmdbService {

    private final TmdbClient tmdbClient;

    private static final String POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500";

    public ContentSearchResultDto searchMovieByExactTitle(String title) {
        TmdbSearchResponse response = tmdbClient.searchMovie(title);

        return response.getResults().stream()
                .filter(movie -> movie.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .map(this::toContentSearchResult)
                .orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));
    }

    private ContentSearchResultDto toContentSearchResult(TmdbMovieDto movie) {
        String directorName = fetchDirectorName(movie.getId());

        return new ContentSearchResultDto(
                String.valueOf(movie.getId()),
                movie.getTitle(),
                directorName,
                movie.getDescription(),
                POSTER_BASE_URL + movie.getPosterPath(),
                parseDate(movie.getReleaseDate()),
                ContentType.MOVIE
        );
    }

    private String fetchDirectorName(int movieId) {
        try {
            TmdbCreditsResponse credits = tmdbClient.fetchCredits(movieId);
            return credits.getCrew().stream()
                    .filter(c -> "Director".equalsIgnoreCase(c.getJob()))
                    .map(TmdbCrewDto::getName)
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null; // 실패하면 creatorName은 null로 유지
        }
    }

    private LocalDateTime parseDate(String date) {
        try {
            return LocalDate.parse(date).atStartOfDay();
        } catch (Exception e) {
            return null;
        }
    }
}
