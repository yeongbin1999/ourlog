package com.back.ourlog.domain.diary.controller;

import com.back.ourlog.domain.diary.dto.DiaryWriteRequestDto;
import com.back.ourlog.domain.diary.entity.Diary;
import com.back.ourlog.domain.diary.repository.DiaryRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class DiaryControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DiaryRepository diaryRepository;

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

    @DisplayName("감상일기 등록 실패 - 제목 없음")
    @Test
    void t2() throws Exception {
        DiaryWriteRequestDto dto = new DiaryWriteRequestDto("", "내용 있음");

        mvc.perform(post("/api/v1/diaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("제목을 입력해주세요."));
    }

    @DisplayName("감상일기 등록 실패 - 내용 없음")
    @Test
    void t3() throws Exception {
        DiaryWriteRequestDto dto = new DiaryWriteRequestDto("제목 있음", "");

        mvc.perform(post("/api/v1/diaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("내용을 입력해주세요."));
    }

    @Test
    @DisplayName("감성일기 조회")
    @Transactional(readOnly = true)
    void t4() throws Exception {
        int id = 1;
        ResultActions resultActions = mvc.perform(
                get("/api/v1/diaries/" + id)
        ).andDo(print());


        resultActions
                .andExpect(handler().handlerType(DiaryController.class))
                .andExpect(handler().methodName("getDiary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("다이어리 1"))
                .andExpect(jsonPath("$.data.rating").value(3.0))
                .andExpect(jsonPath("$.data.contentText").value("이것은 다이어리 1의 본문 내용입니다."))
                .andExpect(jsonPath("$.data.tagNames[0]").isNotEmpty());
    }

//    @Test
//    @DisplayName("감성일기 수정 성공")
//    void t5() throws Exception {
//        int id = 1; // 존재하는 다이어리 ID
//        String body = """
//        {
//            "title": "수정된 다이어리",
//            "contentText": "수정된 내용입니다.",
//            "rating": 4.0,
//            "isPublic": true,
//            "externalId": "MOV123456",
//            "type": "MOVIE",
//            "tagIds": [1, 2],
//            "genreIds": [3],
//            "ottIds": [1]
//        }
//    """;
//
//        mvc.perform(
//                        put("/api/v1/diaries/" + id)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                //.header("Authorization", "Bearer MOCK_ACCESS_TOKEN")
//                                .content(body)
//                ).andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.title").value("수정된 다이어리"))
//                .andExpect(jsonPath("$.data.contentText").value("수정된 내용입니다."))
//                .andExpect(jsonPath("$.data.rating").value(4.0));
//    }

    @Test
    @DisplayName("감성일기 수정 실패 - 존재하지 않는 ID")
    void t6() throws Exception {
        int id = 9999; // 존재하지 않는 ID
        String body = """
        {
            "title": "수정된 다이어리",
            "contentText": "수정된 내용입니다.",
            "rating": 4.0,
            "isPublic": true,
            "externalId": "MOV123456",
            "type": "MOVIE",
            "tagIds": [1, 2],
            "genreIds": [3],
            "ottIds": [1]
        }
    """;

        mvc.perform(
                        put("/api/v1/diaries/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                //.header("Authorization", "Bearer MOCK_ACCESS_TOKEN")
                                .content(body)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("DIARY_001"))
                .andExpect(jsonPath("$.msg").value("존재하지 않는 다이어리입니다."));
    }
    
}