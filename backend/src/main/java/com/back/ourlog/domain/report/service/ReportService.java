package com.back.ourlog.domain.report.service;

import com.back.ourlog.domain.report.entity.Report;
import com.back.ourlog.domain.report.entity.ReportReason;
import com.back.ourlog.domain.report.repository.ReportRepository;
import com.back.ourlog.domain.user.entity.User;
import com.back.ourlog.domain.user.repository.UserRepository;
import com.back.ourlog.domain.banHistory.entity.BanHistory;
import com.back.ourlog.global.common.dto.RsData;
import com.back.ourlog.global.exception.CustomException;
import com.back.ourlog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final UserBanHistoryRepository userBanHistoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final int BAN_THRESHOLD = 5; // 신고 누적 시 밴 기준
    private static final int BAN_DAYS = 7;

    public RsData<?> reportUser(User reporter, Long targetUserId, ReportReason type) {
        if (reporter.getId().equals(targetUserId)) {
            throw new CustomException(ErrorCode.REPORT_SELF_NOT_ALLOWED);
        }

        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        boolean exists = reportRepository.existsByReporterAndTargetAndType(reporter, target, type);
        if (exists) {
            throw new CustomException(ErrorCode.REPORT_DUPLICATE);
        }

        Report report = new Report();
        report.setReporter(reporter);
        report.setTarget(target);
        report.setType(type);
        reportRepository.save(report);

        long recentReports = reportRepository.countRecentReportsForUser(target.getId(), LocalDateTime.now().minusDays(30));
        if (recentReports >= BAN_THRESHOLD) {
            banUser(target, "신고 누적 " + recentReports + "건", LocalDateTime.now().plusDays(BAN_DAYS));
        }

        return RsData.of("S-1", "신고가 접수되었습니다.");
    }

    private void banUser(User target, String reason, LocalDateTime expiredAt) {
        // DB 저장
        BanHistory ban = new BanHistory();
        ban.setUser(target);
        ban.setReason(reason);
        ban.setBannedAt(LocalDateTime.now());
        ban.setExpiredAt(expiredAt);
        userBanHistoryRepository.save(ban);

        // Redis 캐싱
        String key = "ban:user:" + target.getId();
        BanInfo banInfo = new BanInfo(reason, ban.getBannedAt(), expiredAt);
        redisTemplate.opsForValue().set(key, banInfo, Duration.between(LocalDateTime.now(), expiredAt));
    }
}
