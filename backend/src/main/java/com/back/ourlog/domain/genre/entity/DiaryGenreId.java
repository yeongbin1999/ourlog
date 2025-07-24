package com.back.ourlog.domain.genre.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryGenreId implements Serializable {
    private Integer diary;
    private Integer genre;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiaryGenreId)) return false;
        DiaryGenreId that = (DiaryGenreId) o;
        return Objects.equals(diary, that.diary) && Objects.equals(genre, that.genre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(diary, genre);
    }
}
