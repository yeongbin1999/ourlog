package com.back.ourlog.global.security.oauth;

import com.back.ourlog.domain.user.entity.User;
import com.back.ourlog.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); // google, naver, kakao
        OAuthAttributes attributes = OAuthAttributes.of(provider, oAuth2User.getName(), oAuth2User.getAttributes());

        User user = userService.registerOrGetOAuthUser(attributes);

        return new CustomOAuth2User(user, attributes.getAttributes(), attributes.getProviderId());
    }
}
