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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpotifyService {

    private final SpotifyClient spotifyClient;

    public ContentSearchResultDto searchMusicByExactTitle(String title) {
        SpotifySearchResponse response = spotifyClient.searchTrack(title);

        return response.getTracks().getItems().stream()
                .filter(track -> track.getName().equalsIgnoreCase(title))
                .findFirst()
                .map(this::toContentSearchResult)
                .orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));
    }

    private ContentSearchResultDto toContentSearchResult(TrackItem trackItem) {
        String creatorName = trackItem.getArtists().get(0).getName();
        String posterUrl = trackItem.getAlbum().getImages().isEmpty() ? null : trackItem.getAlbum().getImages().get(0).getUrl();
        String releaseDate = trackItem.getAlbum().getReleaseDate();

        LocalDateTime releasedAt = null;
        try {
            releasedAt = LocalDate.parse(releaseDate).atStartOfDay();
        } catch (Exception ignored) {}

        return new ContentSearchResultDto(
                trackItem.getId(),
                trackItem.getName(),
                creatorName,
                null,
                posterUrl,
                releasedAt,
                ContentType.MUSIC
        );
    }
}
