package com.pedia.movie.user.controller;

import com.pedia.movie.user.dto.WishWatchingResponse;
import com.pedia.movie.user.service.WishWatchingService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Log4j2
@RequiredArgsConstructor
public class WishWatchController {

    private final WishWatchingService wishWatchingService;
    String resultType;
    String message;

    @PostMapping("films/{filmId}/wish_watching")
    public String WishWatching( @PathVariable Long filmId, @RequestParam("action") String action, HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("user");
        if(userId == null) {
            resultType = "fail";
            message = "로그인이 필요합니다.";
            redirectAttributes.addFlashAttribute("resultType", resultType);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/films/" + filmId;
        }

        WishWatchingResponse wishWatchingResponse = wishWatchingService.getWishWatchList(userId,filmId,action);

        String nextPage = "redirect:/films/" + filmId;

        String resultType = "success";
        String message = (wishWatchingResponse == null) ? "취소되었습니다." : "갱신되었습니다.";
//        model.addAttribute("wishWatchingResponse", wishWatchingResponse); // 모델에 추가
        System.out.println("check 2");
        redirectAttributes.addFlashAttribute("resultType", resultType);
        redirectAttributes.addFlashAttribute("message", message);
        return nextPage;
    }

}
