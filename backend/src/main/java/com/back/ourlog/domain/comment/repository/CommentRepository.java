package com.back.ourlog.domain.comment.repository;

import com.back.ourlog.domain.comment.entity.Comment;
import com.back.ourlog.domain.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// 특정 일기에 달린 댓글 수를 계산..
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    int countByDiaryId(Integer diaryId);

    // 하나 diary의 댓글정보 - 작성일이 최근 일수록 List 의 상단에 위치하도록
    @Query("select c from Comment c where c.diary = :diary order by c.createdAt DESC")
    List<Comment> findByDiaryOrderByCreatedAtDesc(@Param("diary") Diary diary);
}
