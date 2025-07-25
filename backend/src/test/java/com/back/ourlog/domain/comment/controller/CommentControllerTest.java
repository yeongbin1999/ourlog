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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class CommentControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("댓글 작성")
    void t1() throws Exception{
        Map<String, Object> data = new HashMap<>();
        data.put("content", "안녕하시렵니까?");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(data);

        ResultActions resultActions = mvc.perform(
                post("api/v1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andDo(print());


        resultActions
                .andExpect(handler().handlerType(CommentController.class))
                .andExpect(handler().methodName("writeComment"))
                .andExpect(status().isCreated());
    }
}