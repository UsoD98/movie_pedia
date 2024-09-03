package com.pedia.movie.movie.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class FilmImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long movieId;

    @Column
    private Long Width;

    @Column
    private Long Height;

    @Column(nullable = false)
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "film_movie_id", referencedColumnName = "movieId")
    private Film film;

}
