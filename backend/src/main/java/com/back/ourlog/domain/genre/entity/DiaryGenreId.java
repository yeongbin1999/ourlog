package com.back.ourlog.domain.genre.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryGenreId implements Serializable {

    private Integer diaryId;
    private Integer genreId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiaryGenreId)) return false;
        DiaryGenreId that = (DiaryGenreId) o;
        return Objects.equals(diaryId, that.diaryId) &&
                Objects.equals(genreId, that.genreId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(diaryId, genreId);
    }
}

