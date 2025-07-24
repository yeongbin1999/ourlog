package com.back.ourlog.domain.tag.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryTagId implements Serializable {
    private Integer diary;
    private Integer tag;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiaryTagId)) return false;
        DiaryTagId that = (DiaryTagId) o;
        return Objects.equals(diary, that.diary) && Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(diary, tag);
    }
}
