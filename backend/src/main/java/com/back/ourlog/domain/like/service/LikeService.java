package com.back.ourlog.domain.like.service;

import com.back.ourlog.domain.diary.entity.Diary;
import com.back.ourlog.domain.diary.repository.DiaryRepository;
import com.back.ourlog.domain.like.entity.Like;
import com.back.ourlog.domain.like.repository.LikeRepository;
import com.back.ourlog.domain.user.entity.User;
import com.back.ourlog.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;

    private static final Integer MOCK_USER_ID = 1; // 테스트용 임시 사용자 ID..

    @Transactional  // 좋아요 등록
    public void like(Integer diaryId) {
        if (likeRepository.existsByUserIdAndDiaryId(MOCK_USER_ID, diaryId)) {
            return; // 이미 눌렀는지 체크
        }

        User user = userRepository.findById(MOCK_USER_ID)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("다이어리 없음"));

        Like like = new Like(diary, user);
        likeRepository.save(like);
    }

    @Transactional  // 좋아요 삭제
    public void unlike(Integer diaryId) {
        likeRepository.deleteByUserIdAndDiaryId(MOCK_USER_ID, diaryId);
    }
}
