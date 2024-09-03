package com.pedia.movie.movie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@Getter
public class FilmImgResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("backdrops")
    private List<ImageData> backdrops;

    @Getter
    @Setter
    @Data
    public static class ImageData{

        @JsonProperty("height")
        private Long height;

        @JsonProperty("width")
        private Long width;

        @JsonProperty("file_path")
        private String filePath;

    }
}
