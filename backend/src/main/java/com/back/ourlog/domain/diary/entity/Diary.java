package com.back.ourlog.domain.diary.entity;

import com.back.ourlog.domain.comment.entity.Comment;
import com.back.ourlog.domain.content.entity.Content;
import com.back.ourlog.domain.genre.entity.DiaryGenre;
import com.back.ourlog.domain.like.entity.Like;
import com.back.ourlog.domain.ott.entity.DiaryOtt;
import com.back.ourlog.domain.tag.entity.DiaryTag;
import com.back.ourlog.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Diary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    private String title;
    private String contentText;
    private Float rating;
    private Boolean isPublic;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaryTag> diaryTags = new ArrayList<>();

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaryGenre> diaryGenres = new ArrayList<>();

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaryOtt> diaryOtts = new ArrayList<>();

    public Diary(User user, Content content, String title, String contentText, Float rating, Boolean isPublic) {
        this.user = user;
        this.content = content;
        this.title = title;
        this.contentText = contentText;
        this.rating = rating;
        this.isPublic = isPublic;
    }

}
