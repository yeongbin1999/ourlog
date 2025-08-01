package com.back.ourlog.domain.follow.repository;

import com.back.ourlog.domain.follow.entity.Follow;
import com.back.ourlog.domain.follow.enums.FollowStatus;
import com.back.ourlog.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Integer> {

    // 팔로우 중복 확인..
    boolean existsByFollowerIdAndFolloweeId(Integer followerId, Integer followeeId);

    // 쌍방향 언팔로우 가능..
    @Query("""
    SELECT f FROM Follow f WHERE (f.follower.id = :userId1 AND f.followee.id = :userId2) OR (f.follower.id = :userId2 AND f.followee.id = :userId1)""")
    Optional<Follow> findByUsersEitherDirection(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);


    // 상태 상관없이 전체 조회..
    Optional<Follow> findByFollowerIdAndFolloweeId(Integer followerId, Integer followeeId);

    // 특정 상태(PENDING 등)로 제한해서 조회..
    Optional<Follow> findByFollowerIdAndFolloweeIdAndStatus(Integer followerId, Integer followeeId, FollowStatus status);

    // 내가 팔로우한 유저 목록 (수락된 것만)..
    @Query("SELECT f.followee FROM Follow f WHERE f.follower.id = :userId AND f.status = 'ACCEPTED'")
    List<User> findFollowingsByUserId(@Param("userId") Integer userId);

    // 나를 팔로우한 유저 목록 (수락된 것만)..
    @Query("SELECT f.follower FROM Follow f WHERE f.followee.id = :userId AND f.status = 'ACCEPTED'")
    List<User> findFollowersByUserId(@Param("userId") Integer userId);

    // 내가 보낸 팔로우 요청 목록 (PENDING 상태)..
    @Query("SELECT f FROM Follow f WHERE f.follower.id = :userId AND f.status = 'PENDING'")
    List<Follow> findSentPendingRequestsByUserId(@Param("userId") Integer userId);

    // 내가 받은 팔로우 요청 목록 (PENDING 상태)..
    @Query("SELECT f FROM Follow f WHERE f.followee.id = :userId AND f.status = 'PENDING'")
    List<Follow> findPendingRequestsByUserId(@Param("userId") Integer userId);

}
