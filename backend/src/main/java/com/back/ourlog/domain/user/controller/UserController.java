package com.back.ourlog.domain.user.controller;

import com.back.ourlog.domain.user.dto.UserProfileResponse;
import com.back.ourlog.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
