package com.back.ourlog.external.library.controller;

import com.back.ourlog.domain.content.repository.ContentRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class LibraryControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ContentRepository contentRepository;
    @Test
    @DisplayName("중앙도서관 도서 정보 조회")
    void t1() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "유적");

        String json = objectMapper.writeValueAsString(data);

        ResultActions resultActions = mvc.perform(
                get("/api/v1/library")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(LibraryController.class))
                .andExpect(handler().methodName("getLibraryInfo"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msg").value("도서 정보가 조회되었습니다."))
                .andExpect(jsonPath("$.data[0].title").value("서울 필동2가 21-1번지 유적"));
    }
}