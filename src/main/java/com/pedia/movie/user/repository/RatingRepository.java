package com.pedia.movie.user.repository;

import com.pedia.movie.movie.entity.Film;
import com.pedia.movie.user.entity.Rating;
import com.pedia.movie.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    Rating findByUserAndFilm(User user, Film film);

    List<Rating> findAllByUser(User user);

    @Query("SELECT COUNT(*) FROM Rating r")
    long countAllRatings();

    @Query("SELECT r FROM Rating r JOIN FETCH r.film WHERE r.user.id = :userId")
    List<Rating> findAllByUserIdWithFilm(@Param("userId") Long userId);

    Rating findByUserIdAndFilmId(Long userId, Long filmId);
}
