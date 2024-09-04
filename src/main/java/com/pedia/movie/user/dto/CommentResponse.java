package com.pedia.movie.user.dto;

import com.pedia.movie.user.entity.Comment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponse {

    private Long id;
    private String content;
    private Long filmId;
    private String filmTitle;
    private String filmPosterPath;
    private LocalDateTime createdAt;
    private String username;
    private Double ratingScore;

    public static CommentResponse from(Comment comment) {
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(comment.getId());
        commentResponse.setContent(comment.getContent());
        commentResponse.setFilmId(comment.getFilm().getId());
        commentResponse.setFilmTitle(comment.getFilm().getTitle());
        commentResponse.setFilmPosterPath(comment.getFilm().getPosterPath());
        commentResponse.setCreatedAt(comment.getCreatedAt());
        commentResponse.setUsername(comment.getUser().getName());

        return commentResponse;
    }
}
