package com.back.ourlog.domain.diary.entity;

import com.back.ourlog.domain.comment.entity.Comment;
import com.back.ourlog.domain.content.entity.Content;
import com.back.ourlog.domain.content.entity.ContentType;
import com.back.ourlog.domain.genre.entity.DiaryGenre;
import com.back.ourlog.domain.genre.entity.Genre;
import com.back.ourlog.domain.like.entity.Like;
import com.back.ourlog.domain.ott.entity.DiaryOtt;
import com.back.ourlog.domain.ott.entity.Ott;
import com.back.ourlog.domain.tag.entity.DiaryTag;
import com.back.ourlog.domain.tag.entity.Tag;
import com.back.ourlog.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Diary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true) // User 완성되면 false 활성화
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

    public void update(String title, String contentText, Float rating, Boolean isPublic, String externalId, ContentType type) {
        this.title = title;
        this.contentText = contentText;
        this.rating = rating;
        this.isPublic = isPublic;

        // Content 내 필드 업데이트
        this.content.update(externalId, type);
    }

    public void updateTags(List<Tag> tags) {
        this.diaryTags.removeIf(diaryTag -> true);
        tags.forEach(tag -> this.diaryTags.add(new DiaryTag(this, tag)));
    }

    public void updateGenres(List<Genre> genres) {
        this.diaryGenres.removeIf(diaryGenre -> true);
        genres.forEach(genre -> this.diaryGenres.add(new DiaryGenre(this, genre)));
    }

    public void updateOtts(List<Ott> otts) {
        this.diaryOtts.removeIf(diaryOtt -> true);
        otts.forEach(ott -> this.diaryOtts.add(new DiaryOtt(this, ott)));
    }

    public Comment addComment(User user, String content) {
        Comment comment = new Comment(this, user, content);
        comments.add(comment);

        return comment;
    }
}