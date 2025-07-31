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
import com.back.ourlog.external.library.service.LibraryService;
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
    private final ContentService contentService;
    private final TagRepository tagRepository;
    private final OttRepository ottRepository;
    private final GenreService genreService;
    private final ContentSearchFacade contentSearchFacade;
    private final LibraryService libraryService;

    public Diary writeWithContentSearch(DiaryWriteRequestDto req, User user) {
        // 외부 콘텐츠 검색
        ContentSearchResultDto result = contentSearchFacade.search(req.type(), req.externalId());

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

        // Genre 매핑 (외부 API로부터 자동 추출된 장르)
        if (result.genres() != null) {
            result.genres().forEach(rawGenre -> {
                String genreName;

                // BOOK일 때만 KDC 코드 → 장르명 변환
                if (result.type() == ContentType.BOOK) {
                    genreName = libraryService.mapKdcToGenre(rawGenre);
                } else {
                    genreName = rawGenre; // MOVIE, MUSIC은 그대로 사용
                }

                Genre genre = genreService.findOrCreateByName(genreName);
                diary.getDiaryGenres().add(new DiaryGenre(diary, genre));
            });
        }

        // OTT 매핑 (MOVIE일 때만 처리)
        if (req.ottIds() != null && req.type() == ContentType.MOVIE) {
            req.ottIds().forEach(ottId -> {
                Ott ott = ottRepository.findById(ottId)
                        .orElseThrow(() -> new CustomException(ErrorCode.OTT_NOT_FOUND));
                diary.getDiaryOtts().add(new DiaryOtt(diary, ott));
            });
        }

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
        ContentSearchResultDto result = null;

        if (contentChanged) {
            // 외부 API에서 새 콘텐츠 정보 조회
            result = contentSearchFacade.search(dto.type(), dto.externalId());
            if (result == null || result.externalId() == null) {
                throw new CustomException(ErrorCode.CONTENT_NOT_FOUND);
            }

            // 새 Content 저장 or 조회
            newContent = contentService.saveOrGet(result, dto.type());
            diary.setContent(newContent);

            // 새 장르 추가
            if (result.genres() != null) {
                updateGenres(diary, result.genres());
            }
        }

        // 나머지 필드 업데이트
        diary.update(dto.title(), dto.contentText(), dto.rating(), dto.isPublic());

        // 태그 & OTT 갱신
        updateTags(diary, dto.tagIds());
        updateOtts(diary, dto.ottIds());

        return DiaryResponseDto.from(diary);
    }

    private void updateGenres(Diary diary, List<String> newGenreNames) {
        List<DiaryGenre> current = diary.getDiaryGenres();
        List<String> currentNames = current.stream()
                .map(dg -> dg.getGenre().getName())
                .toList();

        // BOOK일 경우 KDC 코드를 장르 이름으로 변환
        ContentType contentType = diary.getContent().getType();
        List<String> mappedGenreNames = newGenreNames.stream()
                .map(name -> contentType == ContentType.BOOK ? libraryService.mapKdcToGenre(name) : name)
                .toList();

        // 제거할 항목
        List<DiaryGenre> toRemove = current.stream()
                .filter(dg -> !mappedGenreNames.contains(dg.getGenre().getName()))
                .toList();
        diary.getDiaryGenres().removeAll(toRemove);

        // 추가할 항목
        List<String> toAdd = mappedGenreNames.stream()
                .filter(name -> !currentNames.contains(name))
                .toList();
        toAdd.forEach(name -> {
            Genre genre = genreService.findOrCreateByName(name);
            diary.getDiaryGenres().add(new DiaryGenre(diary, genre));
        });
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

    private void updateOtts(Diary diary, List<Integer> newOttIds) {
        // 영화가 아닐 경우 저장 안함
        if (diary.getContent().getType() != ContentType.MOVIE || newOttIds == null) {
            diary.getDiaryOtts().clear(); // 기존 OTT 모두 제거 (없애야 정합성 유지됨)
            return;
        }

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
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new CustomException(ErrorCode.DIARY_NOT_FOUND));

        List<String> tagNames = diary.getDiaryTags().stream()
                .map(diaryTag -> diaryTag.getTag().getName())
                .toList();

        List<String> genreNames = diary.getDiaryGenres().stream()
                .map(dg -> dg.getGenre().getName())
                .toList();

        List<String> ottNames = diary.getDiaryOtts().stream()
                .map(doo -> doo.getOtt().getName())
                .toList();

        return new DiaryDetailDto(diary, tagNames, genreNames, ottNames);
    }

    public void delete(int diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new CustomException(ErrorCode.DIARY_NOT_FOUND));

        diaryRepository.delete(diary);
    }
}
