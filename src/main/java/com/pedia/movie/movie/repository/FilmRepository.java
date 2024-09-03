package com.pedia.movie.movie.repository;

import com.pedia.movie.movie.entity.Film;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmRepository extends JpaRepository<Film, Long> {

    Film findByMovieCd(Long movieCd);
    Film findByMovieId(Long movieId); //detail img_video

}
