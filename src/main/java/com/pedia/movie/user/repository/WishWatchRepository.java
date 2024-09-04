package com.pedia.movie.user.repository;

import com.pedia.movie.movie.entity.Film;
import com.pedia.movie.user.entity.User;
import com.pedia.movie.user.entity.WishWatchList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishWatchRepository extends JpaRepository<WishWatchList,Long> {
    WishWatchList findByUserAndFilm(User user, Film film);

    WishWatchList findByUserIdAndFilmId(Long userId, Long filmId);

}
