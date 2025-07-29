package com.back.ourlog.domain.content.controller;

import com.back.ourlog.domain.content.dto.ContentResponseDto;
import com.back.ourlog.domain.content.dto.LibraryApiResponseDto;
import com.back.ourlog.domain.content.service.ContentService;
import com.back.ourlog.global.rsData.RsData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/contents")
@RequiredArgsConstructor
@Log4j2
public class ContentController {
    private final ContentService contentService;
    @Value("${library.api-key}")
    private String apiKey;

    @GetMapping("/{diaryId}")
    public ResponseEntity<RsData<ContentResponseDto>> getContent(@PathVariable("diaryId") int diaryId) {
        ContentResponseDto res = contentService.getContent(diaryId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(RsData.of("200-1", "%d번 다이어리의 조회 컨텐츠가 조회되었습니다.".formatted(diaryId), res));
    }

    @GetMapping("/library")
    public ResponseEntity<RsData<List<LibraryApiResponseDto>>> callLibraryApi() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        String url = "https://www.nl.go.kr/seoji/SearchApi.do?cert_key=%s".formatted(apiKey) +
                "&result_style=json&page_no=1&page_size=100";

        String data = restTemplate.getForObject(url, String.class);

        Map map = objectMapper.readValue(data, Map.class);

        List<Map<String, Object>> result = (List<Map<String, Object>>) map.get("docs");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        List<LibraryApiResponseDto> res = result.stream()
                .map(item -> {
                    String title = (String) item.get("TITLE");
                    String description = (String) item.get("BOOK_INTRODUCTION");
                    String posterUrl = (String) item.get("TITLE_URL");
                    String releasedAtStr = (String) item.get("PUBLISH_PREDATE");

                    LocalDateTime releasedAt = null;
                    if (releasedAtStr != null && !releasedAtStr.isBlank()) {
                        try {
                            releasedAt = LocalDate.parse(releasedAtStr, formatter).atStartOfDay();
                        } catch (DateTimeParseException e) {
                            log.info("날짜 파싱 실패");
                        }
                    }

                    return new LibraryApiResponseDto(title, description, posterUrl, releasedAt);
                })
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(RsData.of("200-2", "도서관 자료가 조회되었습니다.", res));
    }
}