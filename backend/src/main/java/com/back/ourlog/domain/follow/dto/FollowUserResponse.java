package com.back.ourlog.domain.follow.dto;

import com.back.ourlog.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

// 팔로우 유저 조회 응답 DTO..
@Getter
@AllArgsConstructor
public class FollowUserResponse {
    private Integer userId;
    private String email;
    private String nickname;
    private String profileImageUrl;

    public static FollowUserResponse from(User user) {
        return new FollowUserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImageUrl()
        );
    }
}
