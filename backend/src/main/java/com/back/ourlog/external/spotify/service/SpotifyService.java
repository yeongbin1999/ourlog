package com.back.ourlog.external.spotify.service;

import com.back.ourlog.domain.content.entity.Content;
import com.back.ourlog.domain.content.entity.ContentType;
import com.back.ourlog.external.spotify.client.SpotifyClient;
import com.back.ourlog.external.spotify.dto.SpotifySearchResponse;
import com.back.ourlog.external.spotify.dto.TrackItem;
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

    public Optional<Content> searchMusicByExactTitle(String title) {
        SpotifySearchResponse response = spotifyClient.searchTrack(title);

        return response.getTracks().getItems().stream()
                .filter(track -> track.getName().equalsIgnoreCase(title))
                .findFirst()
                .map(this::toContentFromTrack);
    }

    public List<Content> searchMusicAsContent(String keyword) {
        SpotifySearchResponse response = spotifyClient.searchTrack(keyword);

        return response.getTracks().getItems().stream()
                .map(this::toContentFromTrack)
                .toList();
    }

    private Content toContentFromTrack(TrackItem trackItem) {
        String title = trackItem.getName();
        String creatorName = trackItem.getArtists().get(0).getName();
        String description = null;
        String imageUrl = trackItem.getAlbum().getImages().isEmpty() ? null : trackItem.getAlbum().getImages().get(0).getUrl();
        String spotifyId = trackItem.getId();
        String releaseDate = trackItem.getAlbum().getReleaseDate();

        LocalDateTime releasedAt = null;
        try {
            releasedAt = LocalDate.parse(releaseDate).atStartOfDay();
        } catch (Exception ignored) {}

        return new Content(
                title,
                ContentType.MUSIC,
                creatorName,
                description,
                imageUrl,
                releasedAt,
                spotifyId
        );
    }
}
