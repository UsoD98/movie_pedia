package com.pedia.movie.user.controller;

import com.pedia.movie.user.dto.RatingResponse;
import com.pedia.movie.user.entity.User;
import com.pedia.movie.user.service.RatingService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Log4j2
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    String resultType;
    String message;

    // 평점 등록
    @PostMapping("films/{filmId}/rating")
    public String rateFilm(@PathVariable Long filmId, @RequestParam("score") Double score, HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("user");
        if(userId == null) {
            resultType = "fail";
            message = "로그인이 필요합니다.";
            redirectAttributes.addFlashAttribute("resultType", resultType);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/films/" + filmId;
        }

        int result = ratingService.rateFilm(userId, filmId, score);


        String nextPage = "redirect:/films/" + filmId;
        switch (result) {
            case RatingService.INSERTED:
                resultType = "success";
                message = "평점이 등록되었습니다.";
                break;
            case RatingService.DELETED:
                resultType = "success";
                message = "평점이 삭제되었습니다.";
                break;
            case RatingService.UPDATED:
                resultType = "success";
                message = "평점이 수정되었습니다.";
                break;
        }

        redirectAttributes.addFlashAttribute("resultType", resultType);
        redirectAttributes.addFlashAttribute("message", message);
        return nextPage;
    }

    // 평점 삭제

}
