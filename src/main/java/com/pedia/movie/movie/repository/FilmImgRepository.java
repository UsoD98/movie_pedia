package com.pedia.movie.movie.repository;

import com.pedia.movie.movie.entity.FilmImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilmImgRepository extends JpaRepository<FilmImg,Long> {

    List<FilmImg> findByMovieId(Long movieId);
}
