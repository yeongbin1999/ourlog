package com.back.ourlog.global.client.spotify.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackItem {
    private String name;
    private String id;

    private List<Artist> artists;
    private Album album;

    @JsonProperty("external_urls")
    private ExternalUrls externalUrls;

    @Getter
    @NoArgsConstructor
    public static class Artist {
        private String name;
    }

    @Getter
    @NoArgsConstructor
    public static class Album {
        @JsonProperty("release_date")
        private String releaseDate;

        private List<Image> images;
    }

    @Getter
    @NoArgsConstructor
    public static class Image {
        private String url;
    }

    @Getter
    @NoArgsConstructor
    public static class ExternalUrls {
        private String spotify;
    }
}
