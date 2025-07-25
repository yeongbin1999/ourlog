package com.back.ourlog.domain.statistics.contoroller;

import com.back.ourlog.domain.statistics.controller.ApiV1StatisticsController;
import com.back.ourlog.domain.statistics.service.StatisticsService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class ApiV1StatisticsControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private StatisticsService statisticsService;

    @Test
    @DisplayName("통계 카드 조회")
    void 통계_카드_조회() throws Exception {

        ResultActions resultActions = mvc.perform(
                get("/api/v1/statistics/card")
        ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1StatisticsController.class))
                .andExpect(handler().methodName("getStatisticsCard"))
                .andExpect(status().isOk());

        resultActions
                .andExpect(jsonPath("$.totalDiaryCount").exists())
                .andExpect(jsonPath("$.averageRating").exists())
                .andExpect(jsonPath("$.favoriteGenre").exists())
                .andExpect(jsonPath("$.favoriteGenreCount").exists())
                .andExpect(jsonPath("$.favoriteEmotion").exists())
                .andExpect(jsonPath("$.favoriteEmotionCount").exists());

        // 필요에 따라 응답값 추가 검증

    }
}
