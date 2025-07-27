package com.back.ourlog.domain.tag.entity;

import com.back.ourlog.domain.diary.entity.Diary;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class DiaryTag {
    @EmbeddedId
    private DiaryTagId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("diaryId") // DiaryTagId.diaryId 와 매핑
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId") // DiaryTagId.tagId 와 매핑
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public DiaryTag(Diary diary, Tag tag) {
        this.diary = diary;
        this.tag = tag;
        this.id = new DiaryTagId(diary.getId(), tag.getId()); // PK 세팅
    }
}