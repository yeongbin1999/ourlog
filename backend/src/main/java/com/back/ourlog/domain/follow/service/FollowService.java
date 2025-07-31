package com.back.ourlog.domain.follow.service;

import com.back.ourlog.domain.follow.dto.FollowUserResponse;
import com.back.ourlog.domain.follow.entity.Follow;
import com.back.ourlog.domain.follow.enums.FollowStatus;
import com.back.ourlog.domain.follow.repository.FollowRepository;
import com.back.ourlog.domain.user.entity.User;
import com.back.ourlog.domain.user.repository.UserRepository;
import com.back.ourlog.global.exception.CustomException;
import com.back.ourlog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    // 팔로우..
    @Transactional
    public void follow(Integer followerId, Integer followeeId) {
        if (followerId.equals(followeeId)) {
            throw new CustomException(ErrorCode.CANNOT_FOLLOW_SELF);
        }

        if (followRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new CustomException(ErrorCode.FOLLOW_ALREADY_EXISTS);
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Follow follow = new Follow(follower, followee);
        followRepository.save(follow);

        follower.increaseFollowingsCount();
        followee.increaseFollowersCount();
    }

    // 언팔로우..
    @Transactional
    public void unfollow(Integer followerId, Integer followeeId) {
        Follow follow = followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId)
                .orElseThrow(() -> new CustomException(ErrorCode.FOLLOW_NOT_FOUND));

        followRepository.delete(follow);

        User follower = follow.getFollower();
        User followee = follow.getFollowee();

        follower.decreaseFollowingsCount();
        followee.decreaseFollowersCount();
    }

    // 내가 팔로우한 유저 목록 조회..
    public List<FollowUserResponse> getFollowings(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        List<User> followings = followRepository.findFollowingsByUserId(userId);
        return followings.stream()
                .map(FollowUserResponse::from)
                .toList();
    }

    // 나를 팔로우한 유저 목록 조회..
    public List<FollowUserResponse> getFollowers(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<User> followers = followRepository.findFollowersByUserId(userId);
        return followers.stream()
                .map(FollowUserResponse::from)
                .toList();
    }

    // 팔로우 요청을 수락 상태로 변경..
    @Transactional
    public void acceptFollow(Integer followId) {
        Follow follow = followRepository.findById(followId)
                .orElseThrow(() -> new CustomException(ErrorCode.FOLLOW_NOT_FOUND));

        if (follow.getStatus() == FollowStatus.ACCEPTED) {
            throw new CustomException(ErrorCode.FOLLOW_ALREADY_ACCEPTED);
        }

        if (follow.getStatus() == FollowStatus.REJECTED) {
            throw new CustomException(ErrorCode.FOLLOW_ALREADY_REJECTED);
        }

        follow.accept();
    }

    // 팔로우 요청을 거절 상태로 변경..
    @Transactional
    public void rejectFollow(Integer followId) {
        Follow follow = followRepository.findById(followId)
                .orElseThrow(() -> new CustomException(ErrorCode.FOLLOW_NOT_FOUND));

        if (follow.getStatus() == FollowStatus.REJECTED) {
            throw new CustomException(ErrorCode.FOLLOW_ALREADY_REJECTED);
        }

        if (follow.getStatus() == FollowStatus.ACCEPTED) {
            throw new CustomException(ErrorCode.FOLLOW_ALREADY_ACCEPTED);
        }

        follow.reject();
    }

}
