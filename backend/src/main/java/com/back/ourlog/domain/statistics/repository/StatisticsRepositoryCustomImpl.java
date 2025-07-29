package com.back.ourlog.domain.statistics.repository;

import com.back.ourlog.domain.content.entity.ContentType;
import com.back.ourlog.domain.statistics.dto.TypeLineGraphDto;
import com.back.ourlog.domain.statistics.dto.TypeRankDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatisticsRepositoryCustomImpl implements  StatisticsRepositoryCustom{
    private final EntityManager em;

    @Override
    public List<TypeLineGraphDto> findTypeLineMonthly(Integer userId, LocalDateTime start, LocalDateTime end) {
        String sql =
                "SELECT " +
                "  FORMATDATETIME(d.created_at, 'yyyy-MM') AS axisLabel, " +
                "  c.type AS type,"+
                "  COUNT(*) AS count " +
                "FROM diary d " +
                "JOIN content c ON d.content_id = c.id " +
                "WHERE d.user_id = ? AND d.created_at BETWEEN ? AND ? " +
                "GROUP BY axisLabel, c.type " +
                "ORDER BY axisLabel, c.type";
        List<Object[]> result = em.createNativeQuery(sql)
                .setParameter(1, userId)
                .setParameter(2, start)
                .setParameter(3, end)
                .getResultList();

        return result.stream()
                .map(row -> new TypeLineGraphDto(
                        (String) row[0],
                        ContentType.valueOf((String) row[1]),
                        ((Number) row[2]).longValue()
                ))
                .toList();
    }

    @Override
    public List<TypeLineGraphDto> findTypeLineDaily(Integer userId, LocalDateTime start, LocalDateTime end) {
        String sql =
                "SELECT " +
                "  FORMATDATETIME(d.created_at, 'yyyy-MM-dd') AS axisLabel, " +
                "  c.type AS type, " +
                "  COUNT(*) AS count " +
                "FROM diary d " +
                "JOIN content c ON d.content_id = c.id " +
                "WHERE d.user_id = ? AND d.created_at BETWEEN ? AND ? " +
                "GROUP BY axisLabel, c.type " +
                "ORDER BY axisLabel, c.type";
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter(1, userId)
                .setParameter(2, start)
                .setParameter(3, end)
                .getResultList();

        return rows.stream()
                .map(row -> new TypeLineGraphDto(
                        (String)row[0],
                        ContentType.valueOf((String)row[1]),
                        ((Number)row[2]).longValue()
                ))
                .toList();
    }

    @Override
    public List<TypeRankDto> findTypeRanking(Integer userId, LocalDateTime start, LocalDateTime end) {
        String jpql =
                "select new com.back.ourlog.domain.statistics.dto.TypeRankDto(" +
                        "  c.type, count(d)" +
                        ") " +
                        "from Diary d join d.content c " +
                        "where d.user.id = :uid and d.createdAt between :s and :e " +
                        "group by c.type order by count(d) desc";
        return em.createQuery(jpql, TypeRankDto.class)
                .setParameter("uid", userId)
                .setParameter("s",   start)
                .setParameter("e",   end)
                .getResultList();
    }
}