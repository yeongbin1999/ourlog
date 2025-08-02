package com.back.ourlog.domain.follow.controller;

import com.back.ourlog.domain.follow.dto.FollowUserResponse;
import com.back.ourlog.domain.follow.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/follows")
@Tag(name = "팔로우 API")
public class FollowController {
    private final FollowService followService;

    @PostMapping("/{followeeId}")
    @Operation(summary = "유저 팔로우")
    public ResponseEntity<String> followUser(@RequestParam Integer followerId,
                                             @PathVariable Integer followeeId) {
        followService.follow(followerId, followeeId);
        return ResponseEntity.ok("팔로우 요청했습니다.");
    }

    @DeleteMapping("/{otherUserId}")
    @Operation(summary = "팔로우 관계 끊기 (언팔로우)")
    public ResponseEntity<String> unfollowUser(@RequestParam Integer myUserId,
                                               @PathVariable Integer otherUserId) {
        followService.unfollow(myUserId, otherUserId);
        return ResponseEntity.ok("팔로우 관계를 끊었습니다.");
    }

    @GetMapping("/followings")
    @Operation(summary = "내가 팔로우한 유저 목록 조회", description = "ACCEPTED 상태의 목록을 반환합니다.")
    public ResponseEntity<List<FollowUserResponse>> getFollowings(@RequestParam Integer userId) {
        List<FollowUserResponse> response = followService.getFollowings(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/followers")
    @Operation(summary = "나를 팔로우한 유저 목록 조회", description = "ACCEPTED 상태의 목록을 반환합니다.")
    public ResponseEntity<List<FollowUserResponse>> getFollowers(@RequestParam Integer userId) {
        List<FollowUserResponse> response = followService.getFollowers(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{followId}/accept")
    @Operation(summary = "팔로우 요청 수락")
    public ResponseEntity<String> acceptFollow(@PathVariable Integer followId) {
        followService.acceptFollow(followId);
        return ResponseEntity.ok("팔로우 요청 수락 완료!");
    }

    @DeleteMapping("/{followId}/reject")
    @Operation(summary = "팔로우 요청 거절")
    public ResponseEntity<String> rejectFollow(@PathVariable Integer followId) {
        followService.rejectFollow(followId);
        return ResponseEntity.ok("팔로우 요청 거절 완료!");
    }

    @GetMapping("/sent-requests")
    @Operation(summary = "내가 보낸 팔로우 요청 목록 조회", description = "아직 수락되지 않은 PENDING 상태의 팔로우 요청 목록을 반환합니다.")
    public ResponseEntity<List<FollowUserResponse>> getSentRequests(@RequestParam Integer userId) {
        List<FollowUserResponse> response = followService.getSentPendingRequests(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/requests")
    @Operation(summary = "내가 받은 팔로우 요청 목록 조회", description = "PENDING 상태의 팔로우 요청 목록을 반환합니다.")
    public ResponseEntity<List<FollowUserResponse>> getPendingRequests(@RequestParam Integer userId) {
        List<FollowUserResponse> response = followService.getPendingRequests(userId);
        return ResponseEntity.ok(response);
    }


}
