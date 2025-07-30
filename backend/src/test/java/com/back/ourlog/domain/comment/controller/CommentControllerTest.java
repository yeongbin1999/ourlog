package com.back.ourlog.domain.comment.controller;

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

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class CommentControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Test
    @DisplayName("댓글 작성")
    void t1() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("diaryId", 1);
        data.put("content", "안녕하시렵니까?");

        String json = objectMapper.writeValueAsString(data);

        ResultActions resultActions = mvc.perform(
                post("/api/v1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(CommentController.class))
                .andExpect(handler().methodName("writeComment"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.content").value("안녕하시렵니까?"));
    }

    @Test
    @DisplayName("댓글 작성 - 댓글 내용이 없음")
    void t2() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("diaryId", 1);
        data.put("content", "");

        String json = objectMapper.writeValueAsString(data);

        ResultActions resultActions = mvc.perform(
                post("/api/v1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(CommentController.class))
                .andExpect(handler().methodName("writeComment"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("COMMON_400"))
                .andExpect(jsonPath("$.msg").value("must not be blank"));
    }

    @Test
    @DisplayName("댓글 조회")
    void t3() throws Exception {
        int diaryId = 1;

        ResultActions resultActions = mvc.perform(
                get("/api/v1/comments/" + diaryId)
        ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(CommentController.class))
                .andExpect(handler().methodName("getComments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    @DisplayName("댓글 조회 - 존재하지 않는 diaryId")
    void t4() throws Exception {
        int diaryId = 99999;

        ResultActions resultActions = mvc.perform(
                get("/api/v1/comments/" + diaryId)
        ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(CommentController.class))
                .andExpect(handler().methodName("getComments"))
                .andExpect(status().isNotFound());
    }
}