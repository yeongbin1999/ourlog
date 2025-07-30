package com.back.ourlog.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 유저 프로필 응답 DTO..
@Getter
@AllArgsConstructor
public class UserProfileResponse {
    private String email;
    private String nickname;
    private String profileImageUrl;
    private String bio;
}
