package com.back.ourlog.domain.ott.entity;

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
public class DiaryOttId implements Serializable {

    private Integer diaryId;
    private Integer ottId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiaryOttId)) return false;
        DiaryOttId that = (DiaryOttId) o;
        return Objects.equals(diaryId, that.diaryId) &&
                Objects.equals(ottId, that.ottId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(diaryId, ottId);
    }
}

