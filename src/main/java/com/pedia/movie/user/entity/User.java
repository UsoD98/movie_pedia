package com.pedia.movie.user.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

//CREATE TABLE users
//(
//  id INT AUTO_INCREMENT PRIMARY KEY,
//    name VARCHAR(50) NOT NULL,
//    email VARCHAR(100) NOT NULL UNIQUE,
//    password VARCHAR(100) NOT NULL,
//    followers INT DEFAULT 0,
//    following INT DEFAULT 0,
//    ratings_count INT DEFAULT 0,
//    comments_count INT DEFAULT 0,
//    wish_count INT DEFAULT 0,
//    watching_count INT DEFAULT 0,
//    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
//);
@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;

    // 초기값 0
    @Column(columnDefinition = "int default 0")
    private Integer followers = 0;
    @Column(columnDefinition = "int default 0")
    private Integer followings = 0;
    @Column(columnDefinition = "int default 0")
    private Integer ratingsCount = 0;
    @Column(columnDefinition = "int default 0")
    private Integer commentsCount = 0;
    @Column(columnDefinition = "int default 0")
    private Integer wishCount = 0;
    @Column(columnDefinition = "int default 0")
    private Integer watchingCount = 0;
    // 현재 시간
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "follower")
    private List<Follow> followingUsers;

    @OneToMany(mappedBy = "following")
    private List<Follow> followerUsers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WishWatchList> wishWatchLists;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    // 생성 시간 자동 저장
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    void changeName(String name) {
        this.name = name;
    }

    void changePassword(String password) {
        this.password = password;
    }

    public void incrementFollowers() {
        this.followers++;
    }

    public void decrementFollowers() {
        this.followers--;
    }

    public void incrementFollowings() {
        this.followings++;
    }

    public void decrementFollowings() {
        this.followings--;
    }

    public void incrementRatingsCount() {
        this.ratingsCount++;
    }

    public void decrementRatingsCount() {
        this.ratingsCount--;
    }

    public void incrementCommentsCount() {
        this.commentsCount++;
    }

    void decrementCommentsCount() {
        this.commentsCount--;
    }

    public void incrementWishCount() {
        this.wishCount++;
    }

    public void decrementWishCount() {
        this.wishCount--;
    }

    public void incrementWatchingCount() {
        this.watchingCount++;
    }

    public void decrementWatchingCount() {
        this.watchingCount--;
    }

}
