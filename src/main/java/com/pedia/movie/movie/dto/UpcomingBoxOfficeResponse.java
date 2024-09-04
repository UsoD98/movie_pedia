package com.pedia.movie.movie.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpcomingBoxOfficeResponse {

    private Long id;
    private String title;
    private String posterPath;
    private LocalDate releaseDate;
    private double averageRating;
    private long daysUntil;  // D-day 정보 추가

}
