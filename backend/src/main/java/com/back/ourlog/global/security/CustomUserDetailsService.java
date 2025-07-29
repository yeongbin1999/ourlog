package com.back.ourlog.global.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 여기서 실제로 사용자 정보를 DB에서 조회하고,
        // UserDetails 객체를 반환해야 합니다.
        // 예시:
        throw new UsernameNotFoundException("User not found: " + username);
    }
}

