package com.back.ourlog.domain.diary.service;

import com.back.ourlog.domain.content.entity.Content;
import com.back.ourlog.domain.content.service.ContentService;
import com.back.ourlog.domain.diary.dto.DiaryWriteRequestDto;
import com.back.ourlog.domain.diary.entity.Diary;
import com.back.ourlog.domain.diary.repository.DiaryRepository;
import com.back.ourlog.domain.genre.entity.DiaryGenre;
import com.back.ourlog.domain.genre.entity.Genre;
import com.back.ourlog.domain.genre.repository.GenreRepository;
import com.back.ourlog.domain.ott.entity.DiaryOtt;
import com.back.ourlog.domain.ott.entity.Ott;
import com.back.ourlog.domain.ott.repository.OttRepository;
import com.back.ourlog.domain.tag.entity.DiaryTag;
import com.back.ourlog.domain.tag.entity.Tag;
import com.back.ourlog.domain.tag.repository.TagRepository;
import com.back.ourlog.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final ContentService contentService;
    private final TagRepository tagRepository;
    private final GenreRepository genreRepository;
    private final OttRepository ottRepository;

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

    public Optional<Diary> findById(int id) {
        return diaryRepository.findById(id);
    }

}
