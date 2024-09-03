package com.pedia.movie.movie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class FilmVideoResponse {

    @JsonProperty("id")
    private int id;

    @JsonProperty("results")
    private List<VideoData> results;

    @Getter
    @Setter
    @Data
    public static class VideoData {

        @JsonProperty("name")
        private String name;

        @JsonProperty("key")
        private String key;

        @JsonProperty("size")
        private int size;


    }
}
