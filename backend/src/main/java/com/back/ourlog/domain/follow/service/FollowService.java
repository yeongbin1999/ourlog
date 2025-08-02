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
import java.util.Optional;

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

        // 기존 팔로우 여부 확인..
        Optional<Follow> existing = followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId);
        if (existing.isPresent()) {
            Follow existingFollow = existing.get();
            if (existingFollow.getStatus() == FollowStatus.ACCEPTED || existingFollow.getStatus() == FollowStatus.PENDING) {
                throw new CustomException(ErrorCode.FOLLOW_ALREADY_EXISTS);
            }
        }

        // 상대가 나에게 이미 보낸 요청이 있는 경우 수락..
        Optional<Follow> reverse = followRepository
                .findByFollowerIdAndFolloweeIdAndStatus(followeeId, followerId, FollowStatus.PENDING);

        if (reverse.isPresent()) {
            reverse.get().accept();
            reverse.get().getFollower().increaseFollowingsCount();
            reverse.get().getFollowee().increaseFollowersCount();
            return;
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
    public void unfollow(Integer userId1, Integer userId2) {
        Follow follow = followRepository.findByUsersEitherDirection(userId1, userId2)
                .orElseThrow(() -> new CustomException(ErrorCode.FOLLOW_NOT_FOUND));

        followRepository.delete(follow);

        if (follow.getFollower().getId().equals(userId1)) {
            follow.getFollower().decreaseFollowingsCount();
            follow.getFollowee().decreaseFollowersCount();
        } else {
            follow.getFollower().decreaseFollowingsCount();
            follow.getFollowee().decreaseFollowersCount();
        }
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

    // 내가 보낸 팔로우 요청 목록 (PENDING 상태)..
    public List<FollowUserResponse> getSentPendingRequests(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Follow> sentRequests = followRepository.findSentPendingRequestsByUserId(userId);

        return sentRequests.stream()
                .map(f -> FollowUserResponse.from(f.getFollowee()))
                .toList();
    }

    // 내가 받은 팔로우 요청 목록 (PENDING 상태)..
    public List<FollowUserResponse> getPendingRequests(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Follow> pendingFollows = followRepository.findPendingRequestsByUserId(userId);

        return pendingFollows.stream()
                .map(f -> FollowUserResponse.from(f.getFollower()))
                .toList();
    }
}
