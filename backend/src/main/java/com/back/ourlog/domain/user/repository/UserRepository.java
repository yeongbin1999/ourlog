package com.back.ourlog.domain.user.repository;

import com.back.ourlog.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

}
