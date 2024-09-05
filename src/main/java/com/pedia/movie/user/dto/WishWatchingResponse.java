package com.pedia.movie.user.dto;

import lombok.Data;

@Data
public class WishWatchingResponse {

    private boolean wish;

    private boolean watching;

    private Long filmId;

    private String filmTitle;

    private String filmPosterPath;

}
