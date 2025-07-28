package com.back.ourlog.domain.statistics.contoroller;

import com.back.ourlog.domain.statistics.controller.StatisticsController;
import com.back.ourlog.domain.statistics.dto.MonthlyDiaryCount;
import com.back.ourlog.domain.statistics.dto.StatisticsCardDto;
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

import java.util.List;

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
public class StatisticsControllerTest {

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
                .andExpect(handler().handlerType(StatisticsController.class))
                .andExpect(handler().methodName("getStatisticsCard"))
                .andExpect(status().isOk());

        StatisticsCardDto statisticsCardDto = statisticsService.getStatisticsCardByUserId(1);

        resultActions
                .andExpect(jsonPath("$.totalDiaryCount").value(statisticsCardDto.getTotalDiaryCount()))
                .andExpect(jsonPath("$.averageRating").value(statisticsCardDto.getAverageRating()))
                .andExpect(jsonPath("$.favoriteType").value(statisticsCardDto.getFavoriteType()))
                .andExpect(jsonPath("$.favoriteTypeCount").value(statisticsCardDto.getFavoriteTypeCount()))
                .andExpect(jsonPath("$.favoriteEmotion").value(statisticsCardDto.getFavoriteEmotion()))
                .andExpect(jsonPath("$.favoriteEmotionCount").value(statisticsCardDto.getFavoriteEmotionCount()));

    }

    @Test
    @DisplayName("최근 6개월 월 별 감상 수 조회")
    void 최근_6개월_월_별_감상_수_조회() throws Exception {

        ResultActions resultActions = mvc.perform(
                get("/api/v1/statistics/monthly-diary-graph")
        ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(StatisticsController.class))
                .andExpect(handler().methodName("getLast6MonthsDiaryCounts"))
                .andExpect(status().isOk());

        List<MonthlyDiaryCount> monthlyDiaryCounts = statisticsService.getLast6MonthsDiaryCountsByUser(1);

        for(int i = 0; i < monthlyDiaryCounts.size(); i++) {
            MonthlyDiaryCount monthlyDiaryCount = monthlyDiaryCounts.get(i);
            System.out.println("Period: " + monthlyDiaryCount.getPeriod() + ", Views: " + monthlyDiaryCount.getViews());
            resultActions
                    .andExpect(jsonPath("$.[%d].period".formatted(i)).value(monthlyDiaryCount.getPeriod()))
                    .andExpect(jsonPath("$.[%d].views".formatted(i)).value(monthlyDiaryCount.getViews()));
        }
    }
}
