package com.pedia.movie.user.dto;

import lombok.Data;

@Data
public class RatingResponse {
    private double score;

    private String filmTitle;
    private String filmPosterPath;

}
