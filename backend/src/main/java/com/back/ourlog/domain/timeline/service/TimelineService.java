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

// 공개된 일기 목록 조회, 좋아요 수, 댓글 수, 유저 정보 DTO -> 프론트 전달..
@Service
@RequiredArgsConstructor
public class TimelineService {

    private final TimelineRepository timelineRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    public List<TimelineResponse> getPublicTimeline() {
        List<Diary> diaries = timelineRepository.findPublicDiaries();

        return diaries.stream()
                .map(diary -> new TimelineResponse(
                        diary.getId(),
                        diary.getTitle(),
                        diary.getContentText(),
                        diary.getCreatedAt().toString(),
                        diary.getContent().getPosterUrl(),
                        likeRepository.countByDiaryId(diary.getId()),
                        commentRepository.countByDiaryId(diary.getId()),
                        new TimelineResponse.UserSummary(  // ✅ 유저 정보 생성
                                diary.getUser().getId(),
                                diary.getUser().getNickname(),
                                diary.getUser().getProfileImageUrl()
                        )
                ))
                .collect(Collectors.toList());

    }
}
