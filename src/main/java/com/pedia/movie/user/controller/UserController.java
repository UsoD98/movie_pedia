package com.pedia.movie.user.controller;

import com.pedia.movie.user.dto.UserResponse;
import com.pedia.movie.user.service.RatingService;
import com.pedia.movie.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RatingService ratingService;

    String resultType;
    String message;

    // 회원가입 페이지
    @GetMapping("/register")
    public String showRegister() {
        return "/user/registerForm";
    }

    // 회원가입
    @PostMapping("/register")
    public String registerUser(@RequestParam("name") String name,
                               @RequestParam("email") String email,
                               @RequestParam("password") String password,
                               RedirectAttributes redirectAttributes) {

        int result = userService.registerUser(name, email, password);

        String nextPage = "redirect:/users/register";
        switch (result) {
            case UserService.SUCCESS:
                log.info("회원가입 성공");
                resultType = "success";
                message = "회원가입이 성공적으로 완료되었습니다.";
                nextPage = "redirect:/";
                break;
            case UserService.FAIL:
                log.info("회원가입 실패");
                resultType = "fail";
                message = "회원가입에 실패하였습니다.";
                break;
            case UserService.ALREADY_EXIST:
                log.info("이미 존재하는 이메일");
                resultType = "fail";
                message = "이미 존재하는 이메일입니다.";
                break;
            default:
                log.info("알 수 없는 오류");
                resultType = "fail";
                message = "알 수 없는 오류가 발생했습니다.";
                break;
        }

        redirectAttributes.addFlashAttribute("resultType", resultType);
        redirectAttributes.addFlashAttribute("message", message);
        return nextPage;
    }

    // 로그인 페이지
    @GetMapping("/login")
    public String showLoginForm() {
        return "/user/loginForm";
    }

    // 로그인
    @PostMapping("/login")
    public String loginUser(@RequestParam("email") String email,
                            @RequestParam("password") String password,
                            RedirectAttributes redirectAttributes,
                            HttpSession session) {

        int result = userService.login(email, password);

        String nextPage = "redirect:/users/login";

        switch (result) {
            case UserService.SUCCESS:
                log.info("로그인 성공");
                resultType = "success";
                message = "로그인이 성공적으로 완료되었습니다.";
                nextPage = "redirect:/films";
                session.setMaxInactiveInterval(60 * 30);
                session.setAttribute("user", userService.findIdByEmail(email));
                break;
            case UserService.NOT_MATCH:
                log.info("이메일 혹은 비밀번호가 일치하지 않음");
                resultType = "fail";
                message = "이메일 혹은 비밀번호가 일치하지 않습니다.";
                break;
            case UserService.FAIL:
                log.info("로그인 실패");
                resultType = "fail";
                message = "로그인에 실패하였습니다.";
                break;
            default:
                log.info("알 수 없는 오류");
                resultType = "fail";
                message = "알 수 없는 오류가 발생했습니다.";
                break;
        }

        redirectAttributes.addFlashAttribute("resultType", resultType);
        redirectAttributes.addFlashAttribute("message", message);
        return nextPage;
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logoutUser(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        resultType = "success";
        message = "로그아웃이 성공적으로 완료되었습니다.";
        redirectAttributes.addFlashAttribute("resultType", resultType);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/";
    }

    // 타인 프로필
    @GetMapping("/profile/{id}")
    public String showProfile(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        model.addAttribute("user", userService.findUserById(id));
        if(userService.findUserById(id) == null) {
            resultType = "fail";
            message = "존재하지 않는 사용자입니다.";
            redirectAttributes.addFlashAttribute("resultType", resultType);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/";
        }

        Long currentUserId = (Long) session.getAttribute("user");
        if (currentUserId != null && !currentUserId.equals(id)) {
            boolean isFollowing = userService.isFollowing(currentUserId, id);
            model.addAttribute("isFollowing", isFollowing);
        }

        return "/user/profile";
    }

    // 마이페이지
    @GetMapping("/myPage")
    public String showMyPage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("user");
        model.addAttribute("user", userService.findUserById(userId));
        return "/user/myPage";
    }

    // 팔로우
    @PostMapping("/{followerId}/follow/{followingId}")
    public String followUser(@PathVariable Long followerId, @PathVariable Long followingId) {
        userService.followUser(followerId, followingId);
        return "redirect:/users/profile/" + followingId;
    }

    // 언팔로우
    @PostMapping("/{followerId}/unfollow/{followingId}")
    public String unFollowUser(@PathVariable Long followerId, @PathVariable Long followingId) {
        userService.unFollowUser(followerId, followingId);
        return "redirect:/users/profile/" + followingId;
    }

    @GetMapping("/profile/{id}/followers")
    public String showFollowers(@PathVariable Long id, Model model, HttpSession session) {
        List<UserResponse> followers = userService.getFollowers(id);
        Long currentUserId = (Long) session.getAttribute("user");
        if (currentUserId != null) {
            followers.forEach(follower -> follower.setIsFollowing(userService.isFollowing(currentUserId, follower.getId())));
        }
        model.addAttribute("followers", followers);
        model.addAttribute("profileUserId", id);
        return "/user/followers";
    }

    @GetMapping("/profile/{id}/followings")
    public String showFollowings(@PathVariable Long id, Model model, HttpSession session) {
        List<UserResponse> followings = userService.getFollowings(id);
        Long currentUserId = (Long) session.getAttribute("user");
        if (currentUserId != null) {
            followings.forEach(following -> following.setIsFollowing(userService.isFollowing(currentUserId, following.getId())));
        }
        model.addAttribute("followings", followings);
        model.addAttribute("profileUserId", id);
        return "/user/followings";
    }

    // 평가 내역
    @GetMapping("/profile/{id}/ratings")
    public String showRatings(@PathVariable Long id, Model model) {
        model.addAttribute("ratings", ratingService.getRatings(id));
        model.addAttribute("profileUserId", id);
        return "/user/ratings";
    }
}
