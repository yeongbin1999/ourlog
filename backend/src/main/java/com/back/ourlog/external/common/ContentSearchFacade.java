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

import java.util.List;

@Component
@RequiredArgsConstructor
public class ContentSearchFacade {

    private final SpotifyService spotifyService;
    private final TmdbService tmdbService;
    private final LibraryService libraryService;

    public ContentSearchResultDto search(ContentType type, String externalId) {
        try {
            return switch (type) {
                case MUSIC -> spotifyService.searchMusicByExternalId(externalId);
                case MOVIE -> tmdbService.searchMovieByExternalId(externalId);
                case BOOK -> libraryService.searchBookByExternalId(externalId);
            };
        } catch (Exception e) {
            throw new RuntimeException("externalId로 콘텐츠 검색 중 오류 발생", e);
        }
    }

    public List<ContentSearchResultDto> searchByTitle(ContentType type, String title) {
        try {
            return switch (type) {
                case MUSIC -> spotifyService.searchMusicByTitle(title);
                case MOVIE -> tmdbService.searchMovieByTitle(title);
                case BOOK -> libraryService.searchBookByTitle(title);
            };
        } catch (Exception e) {
            throw new CustomException(ErrorCode.CONTENT_NOT_FOUND);
        }
    }

}
