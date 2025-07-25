package com.back.ourlog.domain.content.repository;

import com.back.ourlog.domain.content.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Integer> {

}
