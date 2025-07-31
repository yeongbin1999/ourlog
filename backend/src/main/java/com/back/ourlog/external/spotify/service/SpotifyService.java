package com.back.ourlog.external.spotify.service;

import com.back.ourlog.domain.content.dto.ContentSearchResultDto;
import com.back.ourlog.domain.content.entity.Content;
import com.back.ourlog.domain.content.entity.ContentType;
import com.back.ourlog.external.spotify.client.SpotifyClient;
import com.back.ourlog.external.spotify.dto.SpotifySearchResponse;
import com.back.ourlog.external.spotify.dto.TrackItem;
import com.back.ourlog.global.exception.CustomException;
import com.back.ourlog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotifyService {

    private final SpotifyClient spotifyClient;

    public ContentSearchResultDto searchMusicByExternalId(String externalId) {
        try {
            String id = externalId.replace("spotify-", "");
            TrackItem track = spotifyClient.getTrackById(id); // Spotify 단건 조회 API

            if (track == null || track.getArtists() == null || track.getArtists().isEmpty()) {
                throw new CustomException(ErrorCode.CONTENT_NOT_FOUND);
            }

            String artistId = track.getArtists().get(0).getId();
            List<String> genres = Optional.ofNullable(spotifyClient.fetchGenresByArtistId(artistId))
                    .orElse(new ArrayList<>());

            return toContentSearchResult(track, genres);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.CONTENT_NOT_FOUND);
        }
    }

    private ContentSearchResultDto toContentSearchResult(TrackItem trackItem, List<String> genres) {
        String creatorName = trackItem.getArtists().get(0).getName();
        String posterUrl = trackItem.getAlbum().getImages().isEmpty() ? null : trackItem.getAlbum().getImages().get(0).getUrl();
        String releaseDate = trackItem.getAlbum().getReleaseDate();

        LocalDateTime releasedAt = null;
        try {
            releasedAt = LocalDate.parse(releaseDate).atStartOfDay();
        } catch (Exception ignored) {}

        return new ContentSearchResultDto(
                "spotify-" + trackItem.getId(),
                trackItem.getName(),
                creatorName,
                null,
                posterUrl,
                releasedAt,
                ContentType.MUSIC,
                genres
        );
    }
}
