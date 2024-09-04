package com.pedia.movie.movie.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DailyAndWeeklyResponse {

    private long Id;

    private String title;
    private String posterPath;
    private int rank;
    private int audiAcc; // 누적 관객 수
    private LocalDate releaseDate;
    private double averageRating;

}
