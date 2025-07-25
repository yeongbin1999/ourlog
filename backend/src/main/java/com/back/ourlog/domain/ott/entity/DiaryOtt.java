package com.back.ourlog.domain.ott.entity;

import com.back.ourlog.domain.diary.entity.Diary;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@IdClass(DiaryOttId.class)
public class DiaryOtt {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", nullable = false)
    private Diary diary;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ott_id", nullable = false)
    private Ott ott;

    public DiaryOtt(Diary diary, Ott ott) {
        this.diary = diary;
        this.ott = ott;
    }
}
