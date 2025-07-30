package com.back.ourlog.domain.diary.controller;

import com.back.ourlog.domain.content.entity.ContentType;
import com.back.ourlog.domain.diary.dto.DiaryWriteRequestDto;
import com.back.ourlog.domain.diary.entity.Diary;
import com.back.ourlog.domain.diary.repository.DiaryRepository;

import com.back.ourlog.domain.genre.entity.Genre;
import com.back.ourlog.domain.genre.repository.GenreRepository;
import com.back.ourlog.domain.ott.entity.Ott;
import com.back.ourlog.domain.ott.repository.OttRepository;
import com.back.ourlog.domain.tag.entity.Tag;
import com.back.ourlog.domain.tag.repository.TagRepository;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private OttRepository ottRepository;

    @Test
    @DisplayName("감상일기 등록 성공")
    void t1() throws Exception {

        String body = """
        {
            "title": "Inception",
            "contentText": "정말 재밌었어요!",
            "rating": 4.8,
            "isPublic": true,
            "type": "MOVIE",
            "tagIds": [1],
            "genreIds": [1],
            "ottIds": [1]
        }
    """;

        ResultActions resultActions = mvc.perform(
                post("/api/v1/diaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        ).andDo(print());

        Diary latestDiary = diaryRepository.findTopByOrderByIdDesc().orElseThrow();

        resultActions
                .andExpect(handler().handlerType(DiaryController.class))
                .andExpect(handler().methodName("writeDiary"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("Inception"));
    }

    @Test
    @DisplayName("감상일기 등록 실패 - 제목 없음")
    void t2() throws Exception {
        String body = """
        {
            "title": "",
            "contentText": "내용 있음",
            "rating": 4.0,
            "isPublic": true,
            "type": "MOVIE",
            "tagIds": [1],
            "genreIds": [1],
            "ottIds": [1]
        }
    """;

        mvc.perform(post("/api/v1/diaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("제목을 입력해주세요."));
    }

    @Test
    @DisplayName("감상일기 등록 실패 - 내용 없음")
    void t3() throws Exception {
        String body = """
        {
            "title": "제목 있음",
            "contentText": "",
            "rating": 3.0,
            "isPublic": true,
            "type": "BOOK",
            "tagIds": [1],
            "genreIds": [1],
            "ottIds": [1]
        }
    """;

        mvc.perform(post("/api/v1/diaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
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

    /*
    @Test
    @DisplayName("감상일기 수정 성공")
    void t5() throws Exception {
        Tag tag1 = tagRepository.save(new Tag("로맨스"));
        Tag tag2 = tagRepository.save(new Tag("액션"));
        Genre genre = genreRepository.save(new Genre("스릴러"));
        Ott ott = ottRepository.save(new Ott("Netflix"));

        DiaryWriteRequestDto requestDto = new DiaryWriteRequestDto(
                "원본 제목",
                "원본 내용",
                true,
                3.5F,
                ContentType.MOVIE,
                List.of(tag1.getId()), // 최초엔 tag1만
                List.of(),
                List.of()
        );

        mvc.perform(post("/api/v1/diaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        // 저장된 Diary ID 사용
        Diary diaryBefore = diaryRepository.findTopByOrderByIdDesc().orElseThrow();
        Integer diaryId = diaryBefore.getId();

        assertThat(diaryBefore.getDiaryTags()).hasSize(1);
        assertThat(diaryBefore.getDiaryTags().get(0).getTag().getId()).isEqualTo(tag1.getId());

        // 수정 요청
        String body = """
    {
        "title": "수정된 다이어리",
        "contentText": "수정된 내용입니다.",
        "rating": 4.0,
        "isPublic": true,
        "externalId": "MOV123456",
        "type": "MOVIE",
        "tagIds": [%d, %d],
        "genreIds": [%d],
        "ottIds": [%d]
    }
    """.formatted(tag1.getId(), tag2.getId(), genre.getId(), ott.getId());

        mvc.perform(put("/api/v1/diaries/" + diaryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("수정된 다이어리"))
                .andExpect(jsonPath("$.data.contentText").value("수정된 내용입니다."))
                .andExpect(jsonPath("$.data.rating").value(4.0));

        // 수정 후 검증
        Diary diaryAfter = diaryRepository.findById(diaryId).orElseThrow();

        List<Integer> tagIds = diaryAfter.getDiaryTags().stream()
                .map(dt -> dt.getTag().getId())
                .toList();
        List<Integer> genreIds = diaryAfter.getDiaryGenres().stream()
                .map(dg -> dg.getGenre().getId())
                .toList();
        List<Integer> ottIds = diaryAfter.getDiaryOtts().stream()
                .map(doo -> doo.getOtt().getId())
                .toList();

        assertThat(tagIds).containsExactlyInAnyOrder(tag1.getId(), tag2.getId());
        assertThat(genreIds).containsExactly(genre.getId());
        assertThat(ottIds).containsExactly(ott.getId());
    }

     */

    @Test
    @DisplayName("감상일기 수정 실패 - 존재하지 않는 태그 ID")
    void t6() throws Exception {
        int id = 1;

        String body = """
        {
            "title": "다이어리",
            "contentText": "내용입니다.",
            "rating": 4.0,
            "isPublic": true,
            "externalId": "MOV123456",
            "type": "MOVIE",
            "tagIds": [999],
            "genreIds": [3],
            "ottIds": [1]
        }
    """;

        mvc.perform(
                        put("/api/v1/diaries/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("TAG_001"))
                .andExpect(jsonPath("$.msg").value("존재하지 않는 태그입니다."));
    }

    @Test
    @DisplayName("감상일기 수정 실패 - 존재하지 않는 장르 ID")
    void t7() throws Exception {
        String body = """
        {
            "title": "다이어리",
            "contentText": "내용입니다.",
            "rating": 4.0,
            "isPublic": true,
            "externalId": "MOV123456",
            "type": "MOVIE",
            "tagIds": [1],
            "genreIds": [999],
            "ottIds": [1]
        }
    """;

        mvc.perform(put("/api/v1/diaries/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("GENRE_001"))
                .andExpect(jsonPath("$.msg").value("존재하지 않는 장르입니다."));
    }

    @Test
    @DisplayName("감상일기 수정 실패 - 존재하지 않는 OTT ID")
    void t8() throws Exception {
        String body = """
        {
            "title": "다이어리",
            "contentText": "내용입니다.",
            "rating": 4.0,
            "isPublic": true,
            "externalId": "MOV123456",
            "type": "MOVIE",
            "tagIds": [1],
            "genreIds": [3],
            "ottIds": [999]
        }
    """;

        mvc.perform(put("/api/v1/diaries/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("OTT_001"))
                .andExpect(jsonPath("$.msg").value("존재하지 않는 OTT입니다."));
    }

    @DisplayName("감상일기 삭제 성공")
    @Test
    void t9() throws Exception {
        int id = 1;
        mvc.perform(delete("/api/v1/diaries/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("일기 삭제 완료"))
                .andExpect(jsonPath("$.resultCode").value("200-0"));
    }

    @DisplayName("감상일기 삭제 실패 - 존재하지 않는 ID")
    @Test
    void t10() throws Exception {
        int id = 9999;
        mvc.perform(delete("/api/v1/diaries/" + id))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("DIARY_001"))
                .andExpect(jsonPath("$.msg").value("존재하지 않는 다이어리입니다."));
    }

}