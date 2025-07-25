package com.back.ourlog.domain.content.service;

import com.back.ourlog.domain.content.entity.Content;
import com.back.ourlog.domain.content.entity.ContentType;
import com.back.ourlog.domain.content.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;

    // 외부 API 연동하면 externalId, type 기준으로 정보 갱신하도록 수정
    public Content getOrCreateContent(String externalId, ContentType type) {
        return contentRepository.findByExternalIdAndType(externalId, type)
                .orElseGet(() -> {
                    Content content = new Content(
                            "제목 없음",      // title (임시)
                            type,
                            "설명 없음",      // description (임시)
                            null,             // 포스터 URL
                            LocalDateTime.now(), // releasedAt
                            externalId
                    );
                    return contentRepository.save(content);
                });
    }
}
