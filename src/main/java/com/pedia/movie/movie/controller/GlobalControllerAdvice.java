package com.pedia.movie.movie.controller;

import com.pedia.movie.user.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final RatingService ratingService;
    @ModelAttribute
    public void addAttributes(Model model) {
        // 모든 요청에 대해 footer라는 속성을 추가합니다.
        model.addAttribute("global_rating",ratingService.getRatingCount());
    }
}
