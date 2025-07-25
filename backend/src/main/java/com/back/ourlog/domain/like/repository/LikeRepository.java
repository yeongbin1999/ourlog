package com.back.ourlog.domain.like.repository;

import com.back.ourlog.domain.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

// 특정 일기에 달린 좋아요 수를 계산..
public interface LikeRepository extends JpaRepository<Like, Integer> {
    int countByDiaryId(Integer diaryId);
}
