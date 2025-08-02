package com.back.ourlog.domain.user.service;

import com.back.ourlog.domain.user.dto.UserProfileResponse;
import com.back.ourlog.domain.user.entity.User;
import com.back.ourlog.domain.user.repository.UserRepository;
import com.back.ourlog.global.exception.CustomException;
import com.back.ourlog.global.exception.ErrorCode;
import com.back.ourlog.global.security.oauth.OAuthAttributes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    
    public User findById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public User registerOrGetOAuthUser(OAuthAttributes attributes) {
        return userRepository.findByProviderAndProviderId(attributes.getProvider(), attributes.getProviderId())
                .orElseGet(() -> registerOAuthUser(attributes));
    }

    private User registerOAuthUser(OAuthAttributes attributes) {
        User user = User.builder()
                .email(attributes.getEmail())
                .nickname(attributes.getName())
                .provider(attributes.getProvider())
                .providerId(attributes.getProviderId())
                .profileImageUrl(attributes.getAttributes().get("profile_image") != null ?
                        attributes.getAttributes().get("profile_image").toString() : null)
                .build();

        return userRepository.save(user);
    }
    
    public UserProfileResponse getUserProfile(Integer userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new UserProfileResponse(
                user.getEmail(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getBio()
        );
    }

    // 검색된 유저 리스트를 프로필 응답 DTO로 변환하여 반환..
    public List<UserProfileResponse> searchUsersByNickname(String keyword) {
        List<User> users = userRepository.findByNicknameContainingIgnoreCase(keyword);
        return users.stream()
                .map(user -> new UserProfileResponse(
                        user.getEmail(),
                        user.getNickname(),
                        user.getProfileImageUrl(),
                        user.getBio()
                ))
                .collect(Collectors.toList());
    }
}
