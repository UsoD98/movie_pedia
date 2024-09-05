package com.pedia.movie.user.repository;

import com.pedia.movie.movie.entity.Film;
import com.pedia.movie.user.entity.Comment;
import com.pedia.movie.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Comment findByUserAndFilm(User user, Film film);

    Comment findByUserIdAndFilmId(Long userId, Long filmId);

    List<Comment> findAllByFilmId(Long filmId);

    List<Comment> findAllByUserId(Long userId);

    @Query("SELECT c FROM Comment c ORDER BY c.createdAt DESC")
    List<Comment> findAllOrderByCreatedAtDesc();

    @Query("SELECT c FROM Comment c JOIN FETCH c.film WHERE c.user.id = :userId")
    List<Comment> findAllByUserIdWithFilm(Long userId);
}
