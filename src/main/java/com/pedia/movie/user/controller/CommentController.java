package com.pedia.movie.user.controller;

import com.pedia.movie.user.dto.CommentResponse;
import com.pedia.movie.user.service.CommentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    String resultType;
    String message;

    // 코멘트 등록 폼
    @GetMapping("/films/{filmId}/comments")
    public String showCommentForm(@PathVariable Long filmId, HttpSession session, RedirectAttributes redirectAttributes, Model model) {

        Long userId = (Long) session.getAttribute("user");
        if(userId == null) {
            resultType = "fail";
            message = "로그인이 필요합니다.";
            redirectAttributes.addFlashAttribute("resultType", resultType);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/films/" + filmId;
        }

        CommentResponse comment = commentService.getComment(userId, filmId);
        if (comment == null) {
            comment = new CommentResponse();
            comment.setFilmId(filmId);
        }
        model.addAttribute("comment", comment);

        return "user/commentForm";
    }

    // 코멘트 등록 확인
    @PostMapping("/films/{filmId}/comments")
    public String createComment(@PathVariable Long filmId, @RequestParam("content") String content, HttpSession session, RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("user");
        if(userId == null) {
            resultType = "fail";
            message = "로그인이 필요합니다.";
            redirectAttributes.addFlashAttribute("resultType", resultType);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/films/" + filmId;
        }

        int result = commentService.createComment(userId, filmId, content);

        if (result == CommentService.INSERTED) {
            resultType = "success";
            message = "코멘트가 등록되었습니다.";
        } else if (result == CommentService.UPDATED) {
            resultType = "success";
            message = "코멘트가 수정되었습니다.";
        } else {
            resultType = "fail";
            message = "코멘트 등록에 실패했습니다.";
        }

        redirectAttributes.addFlashAttribute("resultType", resultType);
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/films/" + filmId;
    }

}
