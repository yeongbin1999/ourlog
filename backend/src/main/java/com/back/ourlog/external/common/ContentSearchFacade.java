package com.back.ourlog.external.common;

import com.back.ourlog.domain.content.dto.ContentSearchResultDto;
import com.back.ourlog.domain.content.entity.Content;
import com.back.ourlog.domain.content.entity.ContentType;
import com.back.ourlog.external.library.service.LibraryService;
import com.back.ourlog.external.spotify.service.SpotifyService;
import com.back.ourlog.external.tmdb.service.TmdbService;
import com.back.ourlog.global.exception.CustomException;
import com.back.ourlog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContentSearchFacade {

    private final SpotifyService spotifyService;
    private final TmdbService tmdbService;
    private final LibraryService libraryService;

    public ContentSearchResultDto search(ContentType type, String title) {
        try {
            if (type == ContentType.MUSIC) {
                return spotifyService.searchMusicByExactTitle(title);

            } else if (type == ContentType.MOVIE) {
                return tmdbService.searchMovieByExactTitle(title);

            } else if (type == ContentType.BOOK) {
                return libraryService.searchBookByExactTitle(title);

            } else {
                throw new CustomException(ErrorCode.CONTENT_NOT_FOUND);
            }

        } catch (Exception e) {
            throw new RuntimeException("콘텐츠 검색 중 오류 발생", e);
        }
    }

}
