package com.back.ourlog.domain.tag.repository;

import com.back.ourlog.domain.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Integer> {
}
