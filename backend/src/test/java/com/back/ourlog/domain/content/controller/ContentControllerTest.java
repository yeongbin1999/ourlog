package com.back.ourlog.domain.content.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class ContentControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("컨텐츠 조회")
    void t1() throws Exception {
        int diaryId = 1;

        ResultActions resultActions = mvc.perform(
            get("/api/v1/contents/" + diaryId)
        ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ContentController.class))
                .andExpect(handler().methodName("getContent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("%d번 다이어리의 조회 컨텐츠가 조회되었습니다.".formatted(diaryId)));
    }
}