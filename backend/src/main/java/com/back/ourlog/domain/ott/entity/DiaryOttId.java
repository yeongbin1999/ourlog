package com.back.ourlog.domain.ott.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryOttId implements Serializable {
    private Integer diary;
    private Integer ott;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiaryOttId)) return false;
        DiaryOttId that = (DiaryOttId) o;
        return Objects.equals(diary, that.diary) && Objects.equals(ott, that.ott);
    }

    @Override
    public int hashCode() {
        return Objects.hash(diary, ott);
    }
}
