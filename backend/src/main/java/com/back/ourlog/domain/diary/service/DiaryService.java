package com.back.ourlog.domain.diary.service;

import com.back.ourlog.domain.content.entity.Content;
import com.back.ourlog.domain.content.service.ContentService;
import com.back.ourlog.domain.diary.dto.DiaryWriteRequestDto;
import com.back.ourlog.domain.diary.entity.Diary;
import com.back.ourlog.domain.diary.repository.DiaryRepository;
import com.back.ourlog.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final ContentService contentService;

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

        diaryRepository.save(diary);

        return diary;
    }
}
