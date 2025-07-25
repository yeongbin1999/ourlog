package com.back.ourlog.domain.diary.controller;

import com.back.ourlog.domain.diary.entity.Diary;
import com.back.ourlog.domain.diary.repository.DiaryRepository;
import com.back.ourlog.domain.diary.service.DiaryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class DiaryControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private DiaryService diaryService;

    @Test
    @DisplayName("감상일기 등록 성공")
    void t1() throws Exception {

        String body = """
            {
                "title": "인생 영화",
                "contentText": "감동이었어요",
                "rating": 4.5,
                "isPublic": true,
                "externalId": "MOV123456",
                "type": "MOVIE",
                "tagIds": [1, 2],
                "genreIds": [3],
                "ottIds": [1]
            }
        """;

        ResultActions resultActions = mvc.perform(
                post("/api/v1/diaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andDo(print());

        Diary latestDiary = diaryRepository.findTopByOrderByIdDesc().orElseThrow();

        resultActions
                .andExpect(handler().handlerType(DiaryController.class))
                .andExpect(handler().methodName("writeDiary"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("인생 영화"));
    }

    @Test
    @DisplayName("감성일기 조회")
    void t2() throws Exception {
        int id = 1;
        ResultActions resultActions = mvc.perform(
                get("/api/v1/diaries/" + id)
        ).andDo(print());


        resultActions
                .andExpect(handler().handlerType(DiaryController.class))
                .andExpect(handler().methodName("getDiary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("다이어리 1"))
                .andExpect(jsonPath("$.rating").value(3.0))
                .andExpect(jsonPath("$.contentText").value("이것은 다이어리 1의 본문 내용입니다."))
                .andExpect(jsonPath("$.tagNames[0]").isNotEmpty());
    }
}