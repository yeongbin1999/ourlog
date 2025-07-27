package com.back.ourlog.domain.tag.entity;

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
public class DiaryTagId implements Serializable {
    private Integer diaryId;  // FK 매핑할 ID
    private Integer tagId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiaryTagId)) return false;
        DiaryTagId that = (DiaryTagId) o;
        return Objects.equals(diaryId, that.diaryId) && Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(diaryId, tagId);
    }
}

