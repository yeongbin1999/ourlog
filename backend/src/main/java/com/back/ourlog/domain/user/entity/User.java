package com.back.ourlog.domain.user.entity;

import com.back.ourlog.domain.comment.entity.Comment;
import com.back.ourlog.domain.diary.entity.Diary;
import com.back.ourlog.domain.follow.entity.Follow;
import com.back.ourlog.domain.like.entity.Like;
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
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    private String profileImageUrl;
    private String bio;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER; // 일반 유저 기본값

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Diary> diaries = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    // 내가 팔로우 한 사람 (팔로잉)
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private final List<Follow> followings = new ArrayList<>();

    // 나를 팔로우 한 사람 (팔로워)
    @OneToMany(mappedBy = "followee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private final List<Follow> followers = new ArrayList<>();

    @Column(nullable = false)
    private Integer followersCount = 0;  // 나를 팔로우하는 사람 수

    @Column(nullable = false)
    private Integer followingsCount = 0; // 내가 팔로우하는 사람 수

    public void increaseFollowersCount() {
        this.followersCount++;
    }

    public void decreaseFollowersCount() {
        if (this.followersCount > 0) this.followersCount--;
    }

    public void increaseFollowingsCount() {
        this.followingsCount++;
    }

    public void decreaseFollowingsCount() {
        if (this.followingsCount > 0) this.followingsCount--;
    }

    public User(String email, String password, String nickname, String profileImageUrl, String bio) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.bio = bio;
    }

}
