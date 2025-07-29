package com.back.ourlog.global.client.spotify;

import com.back.ourlog.domain.content.entity.Content;
import com.back.ourlog.domain.content.entity.ContentType;
import com.back.ourlog.global.client.spotify.dto.SpotifySearchResponse;
import com.back.ourlog.global.client.spotify.dto.SpotifyTokenResponse;
import com.back.ourlog.global.client.spotify.dto.TrackItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class SpotifyClient {

    private final RestTemplate restTemplate;

    @Value("${spotify.client-id}")
    private String clientId;

    @Value("${spotify.client-secret}")
    private String clientSecret;

    private String accessToken;

    @PostConstruct
    public void init() {
        this.accessToken = fetchAccessToken();
    }

    private String fetchAccessToken() {
        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedCredentials);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<SpotifyTokenResponse> response = restTemplate.postForEntity(
                    "https://accounts.spotify.com/api/token",
                    request,
                    SpotifyTokenResponse.class
            );

            return response.getBody().getAccessToken();
        } catch (Exception e) {
            log.error("Spotify access token 요청 실패", e);
            throw new RuntimeException("Spotify API 연동 실패");
        }
    }

    public SpotifySearchResponse searchTrack(String keyword) {
        try {
            return requestSpotify(keyword);
        } catch (HttpClientErrorException.Unauthorized e) {
            log.warn("Spotify accessToken 만료됨. 갱신 후 재요청 시도.");

            // accessToken 갱신
            this.accessToken = fetchAccessToken();

            // 재요청 시도
            try {
                return requestSpotify(keyword);
            } catch (Exception retryException) {
                log.error("재요청도 실패했습니다.", retryException);
                throw new RuntimeException("Spotify API 재요청 실패");
            }
        } catch (Exception e) {
            log.error("Spotify 응답 파싱 실패", e);
            throw new RuntimeException("Spotify API 응답 파싱 실패");
        }
    }

    private SpotifySearchResponse requestSpotify(String keyword) {
        String url = "https://api.spotify.com/v1/search?q=" + UriUtils.encode(keyword, StandardCharsets.UTF_8)
                + "&type=track&limit=5";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<SpotifySearchResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                SpotifySearchResponse.class
        );

        return response.getBody();
    }

    public Content toContentFromTrack(TrackItem trackItem) {
        String title = trackItem.getName();
        String creatorName = trackItem.getArtists().get(0).getName();
        String description = null; // Spotify API does not provide a description for tracks
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
