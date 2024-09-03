package com.pedia.movie.movie.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class FilmVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String videoName;

    @Column(nullable = false)
    private String videoKey;

    @ManyToOne
    @JoinColumn(name = "film_movie_id", referencedColumnName = "movieId")
    private Film film;
}
