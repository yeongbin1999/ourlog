package com.back.ourlog.domain.user.controller;

import com.back.ourlog.domain.user.dto.UserProfileResponse;
import com.back.ourlog.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

//    @GetMapping("/users/me")
//    public ResponseEntity<MyProfileResponse> getMe(@AuthenticationPrincipal UserDetails userDetails) {
//        MyProfileResponse response = userService.getUserInfo(userDetails.getId());
//        return ResponseEntity.ok(response);
//    }

    // 유저 프로필 조회용..
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Integer userId) {
        UserProfileResponse profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    // 닉네임에 키워드가 포함된 유저 목록을 조회하는 검색 API..
    @GetMapping("/users/search")
    public ResponseEntity<List<UserProfileResponse>> searchUsers(@RequestParam String keyword) {
        List<UserProfileResponse> results = userService.searchUsersByNickname(keyword);
        return ResponseEntity.ok(results);
    }
}
