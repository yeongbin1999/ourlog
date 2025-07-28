package com.back.ourlog.domain.diary.service;

import com.back.ourlog.domain.content.entity.Content;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final ContentService contentService;
    private final TagRepository tagRepository;
    private final GenreRepository genreRepository;
    private final OttRepository ottRepository;
    private final TagService tagService;
    private final GenreService genreService;
    private final OttService ottService;

    @Transactional
    public Diary write(DiaryWriteRequestDto req, User user) {
        Content content = contentService.getOrCreateContent(req.externalId(), req.type());

        Diary diary = new Diary(
                null, // 나중에 유저 넣기
                content,
                req.title(),
                req.contentText(),
                req.rating().floatValue(),
                req.isPublic()
        );

        // Tag 매핑
        req.tagIds().forEach(tagId -> {
            Tag tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 태그입니다: " + tagId));

            DiaryTag diaryTag = new DiaryTag(diary, tag);
            diary.getDiaryTags().add(diaryTag);
        });

        // Genre 매핑
        req.genreIds().forEach(genreId -> {
            Genre genre = genreRepository.findById(genreId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장르입니다: " + genreId));
            DiaryGenre diaryGenre = new DiaryGenre(diary, genre);
            diary.getDiaryGenres().add(diaryGenre);
        });

        // OTT 매핑
        req.ottIds().forEach(ottId -> {
            Ott ott = ottRepository.findById(ottId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 OTT입니다: " + ottId));
            DiaryOtt diaryOtt = new DiaryOtt(diary, ott);
            diary.getDiaryOtts().add(diaryOtt);
        });

        return diaryRepository.save(diary);
    }

    public DiaryResponseDto update(int id, DiaryUpdateRequestDto dto) {
        Diary diary = diaryRepository.findById(id)
                .orElseThrow(DiaryNotFoundException::new);

        // TODO: 유저 인증 로직은 이후에 추가 예정

        // 기존 연관관계 완전 삭제
        diary.getDiaryTags().clear();
        diary.getDiaryGenres().clear();
        diary.getDiaryOtts().clear();

        // flush로 영속성 컨텍스트에서 제거
        diaryRepository.flush();

        // 연관 필드 업데이트
        diary.update(dto.title(), dto.contentText(), dto.rating(),
                dto.isPublic(), dto.externalId(), dto.type());

        // 연관관계 다시 설정
        diary.updateTags(tagService.getTagsByIds(dto.tagIds()));
        diary.updateGenres(genreService.getGenresByIds(dto.genreIds()));
        diary.updateOtts(ottService.getOttsByIds(dto.ottIds()));

        return DiaryResponseDto.from(diary);
    }

    public DiaryDetailDto getDiaryDetail(int diaryId) {
        Diary diary = diaryRepository.findById(diaryId).orElseThrow();

        List<String> TagNames = diary.getDiaryTags().stream()
                .map(diaryTag -> diaryTag.getTag().getName())
                .toList();

        return new DiaryDetailDto(diary, TagNames);
    }
}
