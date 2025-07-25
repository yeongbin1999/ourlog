package com.back.ourlog.domain.like.repository;

import com.back.ourlog.domain.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Integer> {

}