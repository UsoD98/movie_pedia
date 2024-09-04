package com.pedia.movie.user.entity;

import com.pedia.movie.movie.entity.Film;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Length(max = 100)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "film_id")
    private Film film;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
