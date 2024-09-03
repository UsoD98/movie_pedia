package com.pedia.movie.movie.repository;

import com.pedia.movie.movie.entity.Film;
import com.pedia.movie.movie.entity.FilmVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilmVideoRepository extends JpaRepository<FilmVideo,Long> {
    List<FilmVideo> findByFilm(Film film);

}
