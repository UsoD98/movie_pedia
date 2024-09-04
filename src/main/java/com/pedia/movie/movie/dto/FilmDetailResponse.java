package com.pedia.movie.movie.dto;

import com.pedia.movie.movie.entity.Film;
import com.pedia.movie.user.dto.WishWatchingResponse;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FilmDetailResponse {
    private Long id;
    private Long movieCd;
    private Long movieId;
    private String title;
    private LocalDate releaseDate;
    private String originCountry;
    private String posterPath;
    private String originalTitle;
    private String overview;
    private String backdropPath;

    private double userScore;

    private int ratingCount;
    private double averageRating;
    private WishWatchingResponse wishWatchingResponse;


    public static FilmDetailResponse from(Film film) {

        FilmDetailResponse filmDetailResponse = new FilmDetailResponse();
        filmDetailResponse.setId(film.getId());
        filmDetailResponse.setMovieCd(film.getMovieCd());
        filmDetailResponse.setMovieId(film.getMovieId());
        filmDetailResponse.setTitle(film.getTitle());
        filmDetailResponse.setReleaseDate(film.getReleaseDate());
        filmDetailResponse.setOriginCountry(film.getOriginCountry());
        filmDetailResponse.setPosterPath(film.getPosterPath());
        filmDetailResponse.setOriginalTitle(film.getOriginalTitle());
        filmDetailResponse.setOverview(film.getOverview());
        filmDetailResponse.setBackdropPath(film.getBackdropPath());

        return filmDetailResponse;
    }
}