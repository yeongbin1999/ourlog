package com.back.ourlog.domain.diary.service;

import com.back.ourlog.domain.content.dto.ContentSearchResultDto;
import com.back.ourlog.domain.content.entity.Content;
import com.back.ourlog.domain.content.service.ContentService;
import com.back.ourlog.domain.diary.dto.DiaryDetailDto;
import com.back.ourlog.domain.diary.dto.DiaryResponseDto;
import com.back.ourlog.domain.diary.dto.DiaryUpdateRequestDto;
import com.back.ourlog.domain.diary.dto.DiaryWriteRequestDto;
import com.back.ourlog.domain.diary.entity.Diary;
import com.back.ourlog.domain.diary.exception.DiaryNotFoundException;
import com.back.ourlog.domain.diary.factory.DiaryFactory;
import com.back.ourlog.domain.diary.mapper.DiaryMapper;
import com.back.ourlog.domain.diary.repository.DiaryRepository;
import com.back.ourlog.domain.genre.service.GenreService;
import com.back.ourlog.domain.ott.repository.OttRepository;
import com.back.ourlog.domain.tag.repository.TagRepository;
import com.back.ourlog.domain.user.entity.User;
import com.back.ourlog.external.common.ContentSearchFacade;
import com.back.ourlog.external.library.service.LibraryService;
import com.back.ourlog.global.exception.CustomException;
import com.back.ourlog.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final ContentService contentService;
    private final GenreService genreService;
    private final TagRepository tagRepository;
    private final OttRepository ottRepository;
    private final ContentSearchFacade contentSearchFacade;
    private final LibraryService libraryService;
    private final DiaryFactory diaryFactory;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final Environment env;

    private static final String CACHE_KEY_PREFIX = "diaryDetail::";

    @Transactional
    public Diary writeWithContentSearch(DiaryWriteRequestDto req, User user) {
        // 외부 콘텐츠 검색
        ContentSearchResultDto result = contentSearchFacade.search(req.type(), req.externalId());
        if (result == null || result.externalId() == null) {
            throw new CustomException(ErrorCode.CONTENT_NOT_FOUND);
        }

        // 콘텐츠 저장 or 조회
        Content content = contentService.saveOrGet(result, req.type());

        // Diary 생성 (연관관계 포함)
        Diary diary = diaryFactory.create(user, content, req.title(), req.contentText(), req.rating(), req.isPublic(), req.tagNames(), result.genres(), req.ottIds());

        // 저장
        return diaryRepository.save(diary);
    }

    @Transactional
    public DiaryResponseDto update(int diaryId, DiaryUpdateRequestDto dto) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(DiaryNotFoundException::new);

        // 콘텐츠 변경 시 외부 API 검색 및 재설정
        Content oldContent = diary.getContent();
        boolean contentChanged = !oldContent.getExternalId().equals(dto.externalId())
                || !oldContent.getType().equals(dto.type());

        if (contentChanged) {
            ContentSearchResultDto result = contentSearchFacade.search(dto.type(), dto.externalId());
            if (result == null || result.externalId() == null) {
                throw new CustomException(ErrorCode.CONTENT_NOT_FOUND);
            }

            Content newContent = contentService.saveOrGet(result, dto.type());
            diary.setContent(newContent);

            if (result.genres() != null) {
                diary.updateGenres(result.genres(), genreService, libraryService);
            }
        }

        // 필드 및 연관관계 업데이트
        diary.update(dto.title(), dto.contentText(), dto.rating(), dto.isPublic());
        diary.updateTags(dto.tagNames(), tagRepository);
        diary.updateOtts(dto.ottIds(), ottRepository);

        // 영속성 반영
        diaryRepository.flush();

        objectRedisTemplate.delete("diaryDetail::" + diaryId);

        return DiaryMapper.toResponseDto(diary);
    }

    public DiaryDetailDto getDiaryDetail(Integer diaryId) {
        String cacheKey = CACHE_KEY_PREFIX + diaryId;

        if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
            Diary diary = diaryRepository.findById(diaryId)
                    .orElseThrow(() -> new DiaryNotFoundException());
            return DiaryDetailDto.of(diary);
        }

        Object cached = objectRedisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return objectMapper.convertValue(cached, DiaryDetailDto.class);
        }

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryNotFoundException());

        DiaryDetailDto dto = DiaryDetailDto.of(diary);
        objectRedisTemplate.opsForValue().set(cacheKey, dto);
        return dto;
    }

    @Transactional
    @CacheEvict(value = "diaryDetail", key = "#diaryId")
    public void delete(int diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new CustomException(ErrorCode.DIARY_NOT_FOUND));

        diaryRepository.delete(diary);
    }
}
