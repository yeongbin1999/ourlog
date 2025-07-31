package com.back.ourlog.domain.follow.repository;

import com.back.ourlog.domain.follow.entity.Follow;
import com.back.ourlog.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Integer> {

    // 팔로우 중복 확인..
    boolean existsByFollowerIdAndFolloweeId(Integer followerId, Integer followeeId);

    // 언팔로우 시 follow 조회용..
    Optional<Follow> findByFollowerIdAndFolloweeId(Integer followerId, Integer followeeId);

    // 내가 팔로우한 유저 목록 (수락된 것만)..
    @Query("SELECT f.followee FROM Follow f WHERE f.follower.id = :userId AND f.status = 'ACCEPTED'")
    List<User> findFollowingsByUserId(@Param("userId") Integer userId);

    // 나를 팔로우한 유저 목록 (수락된 것만)..
    @Query("SELECT f.follower FROM Follow f WHERE f.followee.id = :userId AND f.status = 'ACCEPTED'")
    List<User> findFollowersByUserId(@Param("userId") Integer userId);

}
