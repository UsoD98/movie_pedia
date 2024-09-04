package com.pedia.movie.user.entity;

import com.pedia.movie.movie.entity.Film;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class WishWatchList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "film_id")
    private Film film;

    @Column
    private boolean Wish;

    @Column
    private boolean Watch;
//
//    @PrePersist
//    protected void onCreate() {
//        this.createdAt = LocalDateTime.now();
//    }
//
}
