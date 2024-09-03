package com.pedia.movie.movie.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
@Table(name = "films")
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long movieCd;

    @Column(unique = true, nullable = false)
    private Long movieId;

    @Column(nullable = false)
    private String title;

    private LocalDate releaseDate;

    private String originCountry;

    private String posterPath;

    private String originalTitle;

    @Column(columnDefinition = "TEXT")
    private String overview;

    private String backdropPath;

    @OneToMany(mappedBy ="film", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FilmImg> filmImgSet;

    @OneToMany(mappedBy ="film", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FilmVideo> filmVideoSet;


}
