package com.back.ourlog.external.tmdb.controller;

import com.back.ourlog.domain.content.dto.ContentDto;
import com.back.ourlog.external.tmdb.dto.TmdbMovieDto;
import com.back.ourlog.external.tmdb.service.TmdbService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/test/tmdb")
@RequiredArgsConstructor
public class TmdbTestController {

    private final TmdbService tmdbService;

    @GetMapping("/movies")
    public List<ContentDto> searchMovies(@RequestParam("query") String query) {
        return tmdbService.searchMoviesAsContent(query);
    }
}
