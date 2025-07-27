package com.back.ourlog.domain.ott.entity;

import com.back.ourlog.domain.diary.entity.Diary;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class DiaryOtt {

    @EmbeddedId
    private DiaryOttId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("diaryId")  // DiaryOttId.diaryId 와 매핑
    @JoinColumn(name = "diary_id", nullable = false)
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ottId")  // DiaryOttId.ottId 와 매핑
    @JoinColumn(name = "ott_id", nullable = false)
    private Ott ott;

    public DiaryOtt(Diary diary, Ott ott) {
        this.diary = diary;
        this.ott = ott;
        this.id = new DiaryOttId(diary.getId(), ott.getId()); // PK 세팅
    }
}

