package com.back.ourlog.domain.diary.service;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DiaryServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private OttRepository ottRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("감상일기 등록 -> DiaryTag 생성")
    void t1() throws Exception {

        Tag tag1 = tagRepository.save(new Tag("로맨스"));
        Tag tag2 = tagRepository.save(new Tag("액션"));

        DiaryWriteRequestDto requestDto = new DiaryWriteRequestDto(
                "테스트 제목",
                "테스트 내용",
                true,
                4.0F,
                ContentType.MOVIE,
                List.of(tag1.getId(), tag2.getId()),
                List.of(), // genre
                List.of()  // ott
        );

        mockMvc.perform(post("/api/v1/diaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        Diary savedDiary = diaryRepository.findTopByOrderByIdDesc().orElseThrow();

        // Diary 등록 시간 확인
        System.out.println("createdAt = " + savedDiary.getCreatedAt());
        System.out.println("updatedAt = " + savedDiary.getUpdatedAt());

        assertThat(savedDiary.getCreatedAt()).isNotNull();
        assertThat(savedDiary.getUpdatedAt()).isNotNull();

        assertThat(savedDiary.getDiaryTags()).hasSize(2);
        assertThat(savedDiary.getDiaryTags())
                .extracting(dt -> dt.getTag().getName())
                .containsExactlyInAnyOrder("로맨스", "액션");
    }

    @Test
    @DisplayName("감상일기 등록 -> DiaryGenre 생성")
    void t2() throws Exception {
        Tag dummyTag = tagRepository.save(new Tag("더미"));

        Genre genre1 = genreRepository.save(new Genre("스릴러"));
        Genre genre2 = genreRepository.save(new Genre("판타지"));

        DiaryWriteRequestDto requestDto = new DiaryWriteRequestDto(
                "테스트 제목",
                "테스트 내용",
                true,
                4.0F,
                ContentType.MOVIE,
                List.of(dummyTag.getId()),
                List.of(genre1.getId(), genre2.getId()),
                List.of()
        );

        mockMvc.perform(post("/api/v1/diaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        Diary savedDiary = diaryRepository.findTopByOrderByIdDesc().orElseThrow();

        assertThat(savedDiary.getDiaryGenres()).hasSize(2);
        assertThat(savedDiary.getDiaryGenres())
                .extracting(dg -> dg.getGenre().getName())
                .containsExactlyInAnyOrder("스릴러", "판타지");
    }

    @Test
    @DisplayName("감상일기 등록 -> DiaryOtt 생성")
    void t3() throws Exception {
        Tag dummyTag = tagRepository.save(new Tag("더미"));

        Ott ott1 = ottRepository.save(new Ott("Netflix"));
        Ott ott2 = ottRepository.save(new Ott("Disney+"));

        DiaryWriteRequestDto requestDto = new DiaryWriteRequestDto(
                "테스트 제목",
                "테스트 내용",
                true,
                4.0F,
                ContentType.MOVIE,
                List.of(dummyTag.getId()),
                List.of(), // genre
                List.of(ott1.getId(), ott2.getId())
        );

        mockMvc.perform(post("/api/v1/diaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        Diary savedDiary = diaryRepository.findTopByOrderByIdDesc().orElseThrow();

        assertThat(savedDiary.getDiaryOtts()).hasSize(2);
        assertThat(savedDiary.getDiaryOtts())
                .extracting(do_ -> do_.getOtt().getName())
                .containsExactlyInAnyOrder("Netflix", "Disney+");
    }

}
