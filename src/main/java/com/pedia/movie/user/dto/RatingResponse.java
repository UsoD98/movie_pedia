package com.pedia.movie.user.dto;

import lombok.Data;

@Data
public class RatingResponse {
    private double score;

    private Long filmId;
    private String filmTitle;
    private String filmPosterPath;

}
