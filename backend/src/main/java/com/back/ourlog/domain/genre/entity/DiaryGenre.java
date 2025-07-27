package com.back.ourlog.domain.genre.entity;

import com.back.ourlog.domain.diary.entity.Diary;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class DiaryGenre {

    @EmbeddedId
    private DiaryGenreId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("diaryId")  // DiaryGenreId.diaryId와 매핑
    @JoinColumn(name = "diary_id", nullable = false)
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("genreId")  // DiaryGenreId.genreId와 매핑
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    public DiaryGenre(Diary diary, Genre genre) {
        this.diary = diary;
        this.genre = genre;
        this.id = new DiaryGenreId(diary.getId(), genre.getId()); // PK 세팅
    }
}
