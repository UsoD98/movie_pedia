package com.pedia.movie.movie.repository;

import com.pedia.movie.movie.entity.Film;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilmRepository extends JpaRepository<Film, Long> {

    Film findByMovieCd(Long movieCd);
    Film findByMovieId(Long movieId);   //UpcomingFilms
    List<Film> findByTitleContaining(String title); //SearchFilms

    Film findByTitle(String title);
}
