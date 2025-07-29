package com.back.ourlog.external.library.service;

import com.back.ourlog.external.library.dto.LibraryApiResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LibraryService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${library.api-key}")
    private String libraryApiKey;

    // 책 제목을 받아서 검색
    public List<LibraryApiResponseDto> searchBooks(String bookTitle) throws Exception {
        List<Map<String, Object>> result = getResultFromLibrary(bookTitle);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        return result.stream()
                .map(item -> {
                    String title = (String) item.get("TITLE");
                    String creatorName = (String) item.get("AUTHOR");
                    String description = (String) item.get("BOOK_INTRODUCTION");
                    String posterUrl = (String) item.get("TITLE_URL");
                    String releasedAtStr = (String) item.get("PUBLISH_PREDATE");

                    LocalDateTime releasedAt = null;
                    if (releasedAtStr != null && !releasedAtStr.isBlank()) {
                        try {
                            releasedAt = LocalDate.parse(releasedAtStr, formatter).atStartOfDay();
                        } catch (DateTimeParseException e) {
                            // 예외 처리: 로그를 남기거나 releasedAt을 null로 둠
                            System.out.println("날짜 파싱 실패: " + releasedAtStr);
                        }
                    }
                    // releasedAtStr -> (localDateTime) releasedAt (형 변환 불가능)

                    return new LibraryApiResponseDto(title, creatorName, description, posterUrl, releasedAt);
                })
                .toList();
    }

    // 책 제목으로 도서관 API로부터 데이터를 구해온다.
    private List<Map<String, Object>> getResultFromLibrary(String bookTitle) throws Exception {
        String url = "https://www.nl.go.kr/seoji/SearchApi.do?cert_key=%s".formatted(libraryApiKey) +
                "&result_style=json&page_no=1&page_size=10&title=%s".formatted(bookTitle);

        String data = restTemplate.getForObject(url, String.class);

        Map map = objectMapper.readValue(data, Map.class);

        return (List<Map<String, Object>>) map.get("docs");
    }
}
