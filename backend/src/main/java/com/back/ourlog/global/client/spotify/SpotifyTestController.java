package com.back.ourlog.global.client.spotify;

import com.back.ourlog.global.client.spotify.dto.SpotifySearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test/spotify")
public class SpotifyTestController {

    private final SpotifyClient spotifyClient;

    @GetMapping("/tracks")
    public ResponseEntity<List<Map<String, String>>> testSearch(@RequestParam String q) {
        SpotifySearchResponse response = spotifyClient.searchTrack(q);

        List<Map<String, String>> result = response.getTracks().getItems().stream()
                .map(track -> Map.of(
                        "title", track.getName(),
                        "artist", track.getArtists().get(0).getName(),
                        "image", track.getAlbum().getImages().isEmpty() ? "" : track.getAlbum().getImages().get(0).getUrl(),
                        "spotifyUrl", track.getExternalUrls().getSpotify(),
                        "externalId", track.getId()
                ))
                .toList();

        return ResponseEntity.ok(result);
    }
}
