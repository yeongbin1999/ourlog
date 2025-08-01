package com.back.ourlog.domain.user.repository;

import com.back.ourlog.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    // 닉네임에 키워드가 포함된 유저를 대소문자 구분 없이 조회..
    List<User> findByNicknameContainingIgnoreCase(String keyword);
}
