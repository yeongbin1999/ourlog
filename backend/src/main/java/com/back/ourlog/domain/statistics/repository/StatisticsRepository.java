package com.back.ourlog.domain.statistics.repository;

import com.back.ourlog.domain.diary.entity.Diary;
import com.back.ourlog.domain.statistics.dto.FavoriteEmotionAndCountDto;
import com.back.ourlog.domain.statistics.dto.FavoriteGenreAndCountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatisticsRepository extends JpaRepository<Diary, Integer> {

    @Query("SELECT COUNT(d) FROM Diary d WHERE d.user.id = :userId")
    long getTotalDiaryCountByUserId(@Param("userId") int userId);

    @Query("SELECT AVG(d.rating) FROM Diary d WHERE d.user.id = :userId")
    Optional<Double> getAverageRatingByUserId(@Param("userId") int userId);

    @Query(value =""" 
        SELECT g.name AS favoriteGenre, COUNT(*) AS favoriteGenreCount 
        FROM diary_genre dg
        JOIN diary d ON dg.diary_id = d.id
        JOIN genre g ON dg.genre_id = g.id
        WHERE d.user_id = :userId
        GROUP BY g.name
        ORDER BY favoriteGenreCount DESC
        LIMIT 1""", nativeQuery = true)
    Optional<FavoriteGenreAndCountDto> findFavoriteGenreAndCountByUserId(@Param("userId") int userId);

    @Query(value = """
        SELECT t.name AS favoriteEmotion, COUNT(*) AS favoriteEmotionCount
        FROM diary_tag dt
        JOIN diary d ON dt.diary_id = d.id
        JOIN tag t ON dt.tag_id = t.id
        WHERE d.user_id = :userId
        GROUP BY t.name
        ORDER BY favoriteEmotionCount DESC
        LIMIT 1""", nativeQuery = true)
    Optional<FavoriteEmotionAndCountDto> findFavoriteEmotionAndCountByUserId(@Param("userId") int userId);

}
