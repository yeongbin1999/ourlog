package com.back.ourlog.domain.diary.service;

import com.back.ourlog.domain.content.dto.ContentSearchResultDto;
import com.back.ourlog.domain.content.entity.Content;
import com.back.ourlog.domain.content.entity.ContentType;
import com.back.ourlog.domain.content.repository.ContentRepository;
import com.back.ourlog.domain.content.service.ContentService;
import com.back.ourlog.domain.diary.dto.DiaryDetailDto;
import com.back.ourlog.domain.diary.dto.DiaryResponseDto;
import com.back.ourlog.domain.diary.dto.DiaryUpdateRequestDto;
import com.back.ourlog.domain.diary.dto.DiaryWriteRequestDto;
import com.back.ourlog.domain.diary.entity.Diary;
import com.back.ourlog.domain.diary.exception.DiaryNotFoundException;
import com.back.ourlog.domain.diary.repository.DiaryRepository;
import com.back.ourlog.domain.genre.entity.DiaryGenre;
import com.back.ourlog.domain.genre.entity.Genre;
import com.back.ourlog.domain.genre.repository.GenreRepository;
import com.back.ourlog.domain.genre.service.GenreService;
import com.back.ourlog.domain.ott.entity.DiaryOtt;
import com.back.ourlog.domain.ott.entity.Ott;
import com.back.ourlog.domain.ott.repository.OttRepository;
import com.back.ourlog.domain.ott.service.OttService;
import com.back.ourlog.domain.tag.entity.DiaryTag;
import com.back.ourlog.domain.tag.entity.Tag;
import com.back.ourlog.domain.tag.repository.TagRepository;
import com.back.ourlog.domain.tag.service.TagService;
import com.back.ourlog.domain.user.entity.User;
import com.back.ourlog.external.common.ContentSearchFacade;
import com.back.ourlog.global.exception.CustomException;
import com.back.ourlog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final ContentRepository contentRepository;
    private final ContentService contentService;
    private final TagRepository tagRepository;
    private final GenreRepository genreRepository;
    private final OttRepository ottRepository;
    private final TagService tagService;
    private final GenreService genreService;
    private final OttService ottService;
    private final ContentSearchFacade contentSearchFacade;

    public Diary writeWithContentSearch(DiaryWriteRequestDto req, User user) {
        // 외부 콘텐츠 검색
        ContentSearchResultDto result = contentSearchFacade.search(req.type(), req.title());

        if (result == null || result.externalId() == null) {
            throw new CustomException(ErrorCode.CONTENT_NOT_FOUND);
        }

        // 콘텐츠 저장 or 조회
        Content content = contentService.saveOrGet(result, req.type());

        // Diary 생성
        Diary diary = new Diary(
                user,
                content,
                req.title(),
                req.contentText(),
                req.rating(),
                req.isPublic()
        );

        // Tag 매핑
        req.tagIds().forEach(tagId -> {
            Tag tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> new CustomException(ErrorCode.TAG_NOT_FOUND));
            diary.getDiaryTags().add(new DiaryTag(diary, tag));
        });

        // Genre 매핑
        req.genreIds().forEach(genreId -> {
            Genre genre = genreRepository.findById(genreId)
                    .orElseThrow(() -> new CustomException(ErrorCode.GENRE_NOT_FOUND));
            diary.getDiaryGenres().add(new DiaryGenre(diary, genre));
        });

        // OTT 매핑
        req.ottIds().forEach(ottId -> {
            Ott ott = ottRepository.findById(ottId)
                    .orElseThrow(() -> new CustomException(ErrorCode.OTT_NOT_FOUND));
            diary.getDiaryOtts().add(new DiaryOtt(diary, ott));
        });

        return diaryRepository.save(diary);
    }

    public DiaryResponseDto update(int id, DiaryUpdateRequestDto dto) {
        Diary diary = diaryRepository.findById(id)
                .orElseThrow(DiaryNotFoundException::new);

        // TODO: 유저 인증 로직은 이후에 추가 예정

        // 기존 콘텐츠 비교
        Content oldContent = diary.getContent();
        boolean contentChanged = !oldContent.getExternalId().equals(dto.externalId())
                || !oldContent.getType().equals(dto.type());

        Content newContent = oldContent;

        if (contentChanged) {
            // 외부 API 검색
            ContentSearchResultDto result = contentSearchFacade.search(dto.type(), dto.title());
            if (result == null || result.externalId() == null) {
                throw new CustomException(ErrorCode.CONTENT_NOT_FOUND);
            }

            // 새 Content 저장 or 조회
            newContent = contentService.saveOrGet(result, dto.type());
            diary.setContent(newContent);
        }

        // 필드 업데이트
        diary.update(dto.title(), dto.contentText(), dto.rating(), dto.isPublic());

        // 변경된 연관관계만 처리
        updateTags(diary, dto.tagIds());
        updateGenres(diary, dto.genreIds());
        updateOtts(diary, dto.ottIds());

        return DiaryResponseDto.from(diary);
    }

    private void updateTags(Diary diary, List<Integer> newTagIds) {
        List<DiaryTag> current = diary.getDiaryTags();
        List<Integer> currentTagIds = current.stream()
                .map(dt -> dt.getTag().getId())
                .toList();

        // 제거할 항목
        List<DiaryTag> toRemove = current.stream()
                .filter(dt -> !newTagIds.contains(dt.getTag().getId()))
                .toList();
        diary.getDiaryTags().removeAll(toRemove);

        // 추가할 항목
        List<Integer> toAdd = newTagIds.stream()
                .filter(id -> !currentTagIds.contains(id))
                .toList();
        toAdd.forEach(tagId -> {
            Tag tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> new CustomException(ErrorCode.TAG_NOT_FOUND));
            diary.getDiaryTags().add(new DiaryTag(diary, tag));
        });
    }

    private void updateGenres(Diary diary, List<Integer> newGenreIds) {
        List<DiaryGenre> current = diary.getDiaryGenres();
        List<Integer> currentIds = current.stream()
                .map(dg -> dg.getGenre().getId())
                .toList();

        List<DiaryGenre> toRemove = current.stream()
                .filter(dg -> !newGenreIds.contains(dg.getGenre().getId()))
                .toList();
        diary.getDiaryGenres().removeAll(toRemove);

        List<Integer> toAdd = newGenreIds.stream()
                .filter(id -> !currentIds.contains(id))
                .toList();
        toAdd.forEach(id -> {
            Genre genre = genreRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.GENRE_NOT_FOUND));
            diary.getDiaryGenres().add(new DiaryGenre(diary, genre));
        });
    }

    private void updateOtts(Diary diary, List<Integer> newOttIds) {
        List<DiaryOtt> current = diary.getDiaryOtts();
        List<Integer> currentIds = current.stream()
                .map(doo -> doo.getOtt().getId())
                .toList();

        List<DiaryOtt> toRemove = current.stream()
                .filter(doo -> !newOttIds.contains(doo.getOtt().getId()))
                .toList();
        diary.getDiaryOtts().removeAll(toRemove);

        List<Integer> toAdd = newOttIds.stream()
                .filter(id -> !currentIds.contains(id))
                .toList();
        toAdd.forEach(id -> {
            Ott ott = ottRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.OTT_NOT_FOUND));
            diary.getDiaryOtts().add(new DiaryOtt(diary, ott));
        });
    }

    public DiaryDetailDto getDiaryDetail(int diaryId) {
        Diary diary = diaryRepository.findById(diaryId).orElseThrow();

        List<String> TagNames = diary.getDiaryTags().stream()
                .map(diaryTag -> diaryTag.getTag().getName())
                .toList();

        return new DiaryDetailDto(diary, TagNames);
    }

    public void delete(int diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new CustomException(ErrorCode.DIARY_NOT_FOUND));

        diaryRepository.delete(diary);
    }
}
