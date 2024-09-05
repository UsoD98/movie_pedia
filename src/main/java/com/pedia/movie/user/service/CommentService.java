package com.pedia.movie.user.service;

import com.pedia.movie.movie.entity.Film;
import com.pedia.movie.movie.repository.FilmRepository;
import com.pedia.movie.user.dto.CommentResponse;
import com.pedia.movie.user.entity.Comment;
import com.pedia.movie.user.entity.Rating;
import com.pedia.movie.user.entity.User;
import com.pedia.movie.user.repository.CommentRepository;
import com.pedia.movie.user.repository.RatingRepository;
import com.pedia.movie.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    final static public int INSERTED = 1;
    final static public int DELETED = -1;
    final static public int UPDATED = 0;
    final static public int NOT_FOUND = -2;

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final RatingRepository ratingRepository;

    @Transactional
    public int createComment(Long userId, Long filmId, String content) {
        User user = userRepository.findById(userId).orElse(null);
        Film film = filmRepository.findById(filmId).orElse(null);

        Comment comment = commentRepository.findByUserAndFilm(user, film);
        if (comment == null) {
            // 평가 이력이 없으면 새로 insert
            comment = new Comment();
            comment.setUser(user);
            comment.setFilm(film);
            comment.setContent(content);
            user.incrementCommentsCount();
            commentRepository.save(comment);
            return INSERTED;
        } else {
            // 평가 이력이 있으면 업데이트\
            comment.setContent(content);
            commentRepository.save(comment);
            return UPDATED;
        }
    }

    // 코멘트 삭제
    @Transactional
    public int deleteComment(Long userId, Long filmId) {
        User user = userRepository.findById(userId).orElse(null);
        Film film = filmRepository.findById(filmId).orElse(null);

        Comment comment = commentRepository.findByUserAndFilm(user, film);
        if(comment == null) {
            return NOT_FOUND;
        }

        commentRepository.delete(comment);
        return DELETED;
    }

    // 코멘트 단건 조회
    @Transactional(readOnly = true)
    public CommentResponse getComment(Long userId, Long filmId) {
        User user = userRepository.findById(userId).orElse(null);
        Film film = filmRepository.findById(filmId).orElse(null);

        Comment comment = commentRepository.findByUserAndFilm(user, film);

        if(comment == null) {
            return null;
        }

        return createCommentResponse(comment);
    }

    // 한 유저에 대한 모든 코멘트 조회
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByUserId(Long userId) {
        return commentRepository.findAllByUserIdWithFilm(userId).stream()
                .map(this::createCommentResponse)
                .collect(Collectors.toList());
    }

    private CommentResponse createCommentResponse(Comment comment) {
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(comment.getId());
        commentResponse.setContent(comment.getContent());
        commentResponse.setFilmId(comment.getFilm().getId());
        commentResponse.setFilmTitle(comment.getFilm().getTitle());
        commentResponse.setFilmPosterPath(comment.getFilm().getPosterPath());
        commentResponse.setCreatedAt(comment.getCreatedAt());
        commentResponse.setUsername(comment.getUser().getName());

        // Rating 정보 조회 및 설정
        Rating rating = ratingRepository.findByUserAndFilm(comment.getUser(), comment.getFilm());
        if (rating != null) {
            commentResponse.setRatingScore(rating.getScore());
        } else {
            commentResponse.setRatingScore(null);
        }

        return commentResponse;
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        List<Comment> comments = commentRepository.findAllByUserIdWithFilm(userId);

        return comments.stream()
                .map(this::createCommentResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByFilmId(Long filmId) {
        return commentRepository.findAllByFilmId(filmId).stream()
                .map(this::createCommentResponse)
                .collect(Collectors.toList());
    }

    // 코멘트 전체 조회
    @Transactional(readOnly = true)
    public List<CommentResponse> getAllCommentsOrderByDesc() {
        return commentRepository.findAllOrderByCreatedAtDesc().stream()
                .map(this::createCommentResponse)
                .collect(Collectors.toList());
    }
}
