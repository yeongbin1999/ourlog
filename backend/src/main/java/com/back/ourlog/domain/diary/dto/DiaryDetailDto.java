package com.back.ourlog.domain.diary.dto;

import com.back.ourlog.domain.diary.entity.Diary;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DiaryDetailDto {
    private String title;
    private float rating;
    private String contentText;
    private List<String> tagNames;

    public DiaryDetailDto(Diary diary, List<String> tagNames) {
        title = diary.getTitle();
        rating = diary.getRating();
        contentText = diary.getContentText();
        this.tagNames = tagNames;
    }
}
