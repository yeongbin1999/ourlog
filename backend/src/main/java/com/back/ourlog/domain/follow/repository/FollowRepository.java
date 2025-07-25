package com.back.ourlog.domain.follow.repository;

import com.back.ourlog.domain.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Integer> {

}
