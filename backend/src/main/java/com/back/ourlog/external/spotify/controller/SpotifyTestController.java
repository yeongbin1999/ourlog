package com.back.ourlog.external.spotify.controller;

import com.back.ourlog.domain.content.dto.ContentDto;
import com.back.ourlog.domain.content.entity.Content;
import com.back.ourlog.external.spotify.service.SpotifyService;
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

    private final SpotifyService spotifyService;

    @GetMapping("/tracks")
    public ResponseEntity<List<ContentDto>> testSearch(@RequestParam String q) {
        List<Content> contents = spotifyService.searchMusicAsContent(q);
        List<ContentDto> result = contents.stream().map(ContentDto::from).toList();
        return ResponseEntity.ok(result);
    }

}
