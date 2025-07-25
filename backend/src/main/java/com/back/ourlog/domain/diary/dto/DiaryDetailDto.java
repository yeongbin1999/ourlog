package com.back.ourlog.domain.diary.dto;

import com.back.ourlog.domain.diary.entity.Diary;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DiaryDetailDto {
    String title;
    float rating;
    String contentText;

    public DiaryDetailDto(Diary diary) {
        title = diary.getTitle();
        rating = diary.getRating();
        contentText = diary.getContentText();
    }
}
