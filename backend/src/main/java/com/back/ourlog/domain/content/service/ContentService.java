package com.back.ourlog.domain.content.service;

import com.back.ourlog.domain.content.dto.ContentResponseDto;
import com.back.ourlog.domain.content.dto.LibraryApiResponseDto;
import com.back.ourlog.domain.content.entity.Content;
import com.back.ourlog.domain.content.entity.ContentType;
import com.back.ourlog.domain.content.repository.ContentRepository;
import com.back.ourlog.domain.diary.entity.Diary;
import com.back.ourlog.domain.diary.repository.DiaryRepository;
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
public class ContentService {
    private final DiaryRepository diaryRepository;
    private final ContentRepository contentRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${library.api-key}")
    private String libraryApiKey;

    // 외부 API 연동하면 externalId, type 기준으로 정보 갱신하도록 수정
    public Content getOrCreateContent(String externalId, ContentType type) {
        return contentRepository.findByExternalIdAndType(externalId, type)
                .orElseGet(() -> {
                    Content content = new Content(
                            "제목 없음",
                            type,
                            "제작자",
                            "설명 없음",
                            null,
                            LocalDateTime.now(),
                            externalId
                    );
                    return contentRepository.save(content);
                });
    }

    public ContentResponseDto getContent(int diaryId) {
        Diary diary = diaryRepository.findById(diaryId).orElseThrow();
        Content content = diary.getContent();

        return new ContentResponseDto(content);
    }


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
    public List<Map<String, Object>> getResultFromLibrary(String bookTitle) throws Exception {
        String url = "https://www.nl.go.kr/seoji/SearchApi.do?cert_key=%s".formatted(libraryApiKey) +
                "&result_style=json&page_no=1&page_size=10&title=%s".formatted(bookTitle);

        String data = restTemplate.getForObject(url, String.class);

        Map map = objectMapper.readValue(data, Map.class);

        return (List<Map<String, Object>>) map.get("docs");
    }
}
