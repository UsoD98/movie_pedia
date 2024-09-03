package com.pedia.movie.movie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class UpcomingResponse {

    @JsonProperty("dates")
    private Dates dates;

    @JsonProperty("page")
    private int page;

    @JsonProperty("total_pages")
    private int totalPages;

    @JsonProperty("results")
    private List<Movie> results;

    @Data
    public static class Dates {
        @JsonProperty("maximum")
        private String maximum;

        @JsonProperty("minimum")
        private String minimum;
    }

    @Data
    public static class Movie {
        @JsonProperty("adult")
        private boolean adult;

        @JsonProperty("backdrop_path")
        private String backdropPath;

        @JsonProperty("genre_ids")
        private List<Integer> genreIds;

        @JsonProperty("id")
        private Long id;

        @JsonProperty("original_language")
        private String originalLanguage;

        @JsonProperty("original_title")
        private String originalTitle;

        @JsonProperty("overview")
        private String overview;

        @JsonProperty("popularity")
        private double popularity;

        @JsonProperty("poster_path")
        private String posterPath;

        @JsonProperty("release_date")
        private String releaseDate;

        @JsonProperty("title")
        private String title;

        @JsonProperty("video")
        private boolean video;

        @JsonProperty("vote_average")
        private double voteAverage;

        @JsonProperty("vote_count")
        private int voteCount;
    }
}