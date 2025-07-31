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

    @DeleteMapping("/{followeeId}")
    @Operation(summary = "유저 언팔로우")
    public ResponseEntity<String> unfollowUser(@RequestParam Integer followerId,
                                               @PathVariable Integer followeeId) {
        followService.unfollow(followerId, followeeId);
        return ResponseEntity.ok("언팔로우 했습니다.");
    }

    @GetMapping("/followings")
    @Operation(summary = "내가 팔로우한 유저 목록 조회")
    public ResponseEntity<List<FollowUserResponse>> getFollowings(@RequestParam Integer userId) {
        List<FollowUserResponse> response = followService.getFollowings(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/followers")
    @Operation(summary = "나를 팔로우한 유저 목록 조회")
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

}
