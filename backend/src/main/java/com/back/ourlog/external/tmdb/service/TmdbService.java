package com.back.ourlog.external.tmdb.service;

import com.back.ourlog.domain.content.dto.ContentSearchResultDto;
import com.back.ourlog.domain.content.entity.ContentType;
import com.back.ourlog.external.tmdb.client.TmdbClient;
import com.back.ourlog.external.tmdb.dto.*;
import com.back.ourlog.global.exception.CustomException;
import com.back.ourlog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TmdbService {

    private final TmdbClient tmdbClient;

    private static final String POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500";

    public ContentSearchResultDto searchMovieByExternalId(String externalId) {
        try {
            String id = externalId.replace("tmdb-", "");
            TmdbMovieDto movie = tmdbClient.fetchMovieById(id);
            return toContentSearchResult(movie);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.CONTENT_NOT_FOUND);
        }
    }

    private ContentSearchResultDto toContentSearchResult(TmdbMovieDto movie) {
        String directorName = fetchDirectorName(movie.getId());
        List<String> genres = extractGenresFromTmdb(movie.getGenres());


        return new ContentSearchResultDto(
                "tmdb-" + movie.getId(),
                movie.getTitle(),
                directorName,
                movie.getDescription(),
                POSTER_BASE_URL + movie.getPosterPath(),
                parseDate(movie.getReleaseDate()),
                ContentType.MOVIE,
                genres
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

    private static final Map<Integer, String> TMDB_GENRE_MAP = Map.ofEntries(
            Map.entry(28, "액션"),
            Map.entry(12, "모험"),
            Map.entry(16, "애니메이션"),
            Map.entry(35, "코미디"),
            Map.entry(80, "범죄"),
            Map.entry(99, "다큐멘터리"),
            Map.entry(18, "드라마"),
            Map.entry(10751, "가족"),
            Map.entry(14, "판타지"),
            Map.entry(36, "역사"),
            Map.entry(27, "공포"),
            Map.entry(10402, "음악"),
            Map.entry(9648, "미스터리"),
            Map.entry(10749, "로맨스"),
            Map.entry(878, "SF"),
            Map.entry(10770, "TV 영화"),
            Map.entry(53, "스릴러"),
            Map.entry(10752, "전쟁"),
            Map.entry(37, "서부")
    );

    private List<String> extractGenresFromTmdb(List<TmdbGenreDto> genreDtos) {
        return genreDtos.stream()
                .map(dto -> TMDB_GENRE_MAP.get(dto.getId()))
                .filter(Objects::nonNull)
                .toList();
    }

}
