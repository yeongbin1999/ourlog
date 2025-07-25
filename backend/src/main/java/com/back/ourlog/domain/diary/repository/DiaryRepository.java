package com.back.ourlog.domain.diary.repository;

import com.back.ourlog.domain.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository  extends JpaRepository<Diary, Integer> {

}
