package com.back.ourlog.global.security.oauth;

import com.back.ourlog.domain.user.entity.User;
import com.back.ourlog.global.security.service.CustomUserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class CustomOAuth2User extends CustomUserDetails implements OAuth2User {

    private final Map<String, Object> attributes;
    private final String provider;
    private final String providerId;

    public CustomOAuth2User(User user, Map<String, Object> attributes, String provider, String providerId) {
        super(user);
        this.attributes = attributes;
        this.provider = provider;
        this.providerId = providerId;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return provider + "_" + providerId;
    }
}
