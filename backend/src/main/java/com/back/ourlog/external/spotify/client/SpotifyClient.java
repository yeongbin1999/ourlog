package com.back.ourlog.external.spotify.client;

import com.back.ourlog.external.spotify.dto.SpotifySearchResponse;
import com.back.ourlog.external.spotify.dto.SpotifyTokenResponse;
import jakarta.annotation.PostConstruct;
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
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpotifyClient {

    private final RestTemplate restTemplate;

    @Value("${spotify.client-id}")
    private String clientId;

    @Value("${spotify.client-secret}")
    private String clientSecret;

    private String accessToken;
    private LocalDateTime tokenExpireAt;

    @PostConstruct
    public void init() {
        updateAccessToken(); // 초기 토큰 발급 및 만료 시각 설정
    }

    public SpotifySearchResponse searchTrack(String keyword) {
        // 만료 시각 선제 체크
        if (tokenExpireAt == null || LocalDateTime.now().isAfter(tokenExpireAt)) {
            log.info("Spotify accessToken 만료됨. 선제 갱신.");
            updateAccessToken();
        }

        try {
            return requestSpotify(keyword);
        } catch (HttpClientErrorException.Unauthorized e) {
            log.warn("갱신했지만 여전히 Unauthorized. 강제 갱신 후 재시도.");
            updateAccessToken(); // 만료 정보가 틀렸을 경우 재갱신
            return requestSpotify(keyword);
        } catch (Exception e) {
            log.error("Spotify 응답 파싱 실패", e);
            throw new RuntimeException("Spotify API 응답 파싱 실패");
        }
    }

    private void updateAccessToken() {
        SpotifyTokenResponse response = fetchAccessToken();
        this.accessToken = response.getAccessToken();

        // 토큰 만료 시간 저장 (10초 여유)
        this.tokenExpireAt = LocalDateTime.now().plusSeconds(response.getExpiresIn() - 10);

        log.info("Spotify accessToken 갱신 성공, 만료 시각: {}", tokenExpireAt);
    }

    private SpotifyTokenResponse fetchAccessToken() {
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

            return response.getBody();
        } catch (Exception e) {
            log.error("Spotify access token 요청 실패", e);
            throw new RuntimeException("Spotify API 연동 실패");
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
}
