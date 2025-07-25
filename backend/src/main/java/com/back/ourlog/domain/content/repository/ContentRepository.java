package com.back.ourlog.domain.content.repository;

import com.back.ourlog.domain.content.entity.Content;
<<<<<<< HEAD
import com.back.ourlog.domain.content.entity.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContentRepository extends JpaRepository<Content, Integer> {
    Optional<Content> findByExternalIdAndType(String externalId, ContentType type);
=======
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Integer> {

>>>>>>> origin/dev
}
