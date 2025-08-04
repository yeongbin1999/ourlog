package com.back.ourlog.domain.timeline.service;

import com.back.ourlog.domain.comment.repository.CommentRepository;
import com.back.ourlog.domain.diary.entity.Diary;
import com.back.ourlog.domain.like.repository.LikeRepository;
import com.back.ourlog.domain.timeline.dto.TimelineResponse;
import com.back.ourlog.domain.timeline.repository.TimelineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// 공개된 일기 목록 조회, 좋아요 수, 좋아요 여부, 댓글 수, 유저 정보 DTO -> 프론트 전달..
@Service
@RequiredArgsConstructor
public class TimelineService {

    private final TimelineRepository timelineRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private static final Integer MOCK_USER_ID = 1; // 테스트용 임시 사용자 ID..

    public List<TimelineResponse> getPublicTimeline() {
        List<Diary> diaries = timelineRepository.findPublicDiaries();

        return diaries.stream()
                .map(diary -> new TimelineResponse(
                        diary.getId(),
                        diary.getTitle(),
                        diary.getContentText(),
                        diary.getCreatedAt().toString(),
                        diary.getContent().getPosterUrl(),
                        likeRepository.countByDiaryId(diary.getId()),   // 좋아요 개수..
                        commentRepository.countByDiaryId(diary.getId()),    // 댓글 개수..
                        likeRepository.existsByUserIdAndDiaryId(MOCK_USER_ID, diary.getId()),   // 좋아요 여부..
                        new TimelineResponse.UserSummary(
                                diary.getUser().getId(),
                                diary.getUser().getNickname(),
                                diary.getUser().getProfileImageUrl()
                        )
                ))
                .collect(Collectors.toList());

    }
}
