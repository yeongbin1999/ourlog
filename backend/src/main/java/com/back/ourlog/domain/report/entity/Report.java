package com.back.ourlog.domain.report.entity;

import com.back.ourlog.domain.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
public class Report {
    @Id
    @GeneratedValue
    private Integer id;

    @Enumerated(EnumType.STRING)
    private ReportReason type;

    @ManyToOne(fetch = LAZY)
    private User reporter;

    @ManyToOne(fetch = LAZY)
    private User target;

    private LocalDateTime reportedAt;

    // 중복 신고 방지를 위한 유니크 인덱스
    @PrePersist
    public void prePersist() {
        this.reportedAt = LocalDateTime.now();
    }
}

