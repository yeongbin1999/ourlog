package com.back.ourlog.domain.banHistory.entity;

import com.back.ourlog.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@Entity
public class BanHistory {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = LAZY)
    private User user;

    private String reason;

    private LocalDateTime bannedAt;

    private LocalDateTime expiredAt; // null이면 영구정지

    public boolean isActiveNow() {
        return expiredAt == null || expiredAt.isAfter(LocalDateTime.now());
    }
}

