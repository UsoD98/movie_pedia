package com.pedia.movie.user.controller;

import com.pedia.movie.user.dto.CommentResponse;
import com.pedia.movie.user.dto.UserResponse;
import com.pedia.movie.user.dto.WishWatchingResponse;
import com.pedia.movie.user.service.CommentService;
import com.pedia.movie.user.service.RatingService;
import com.pedia.movie.user.service.UserService;
import com.pedia.movie.user.service.WishWatchingService;
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
    private final CommentService commentService;
    private final WishWatchingService wishWatchingService;

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

    @GetMapping("/modifyPw")
    public String modifypw(){
        return "/user/modifyPw";
    }
    @PostMapping("/modifyPw")
    public String modifyPw(@RequestParam("password") String password,
                           @RequestParam("new_password") String new_password,
                           @RequestParam("new_password_check") String new_password_check,
                           RedirectAttributes redirectAttributes,
                           HttpSession httpSession,
                           Model model) {
        Long userId = (Long) httpSession.getAttribute("user");
        int result = userService.modifyPW(userId,password,new_password,new_password_check);

        String nextPage = "redirect:/users/modifyPw";
        switch (result) {
            case UserService.SUCCESS:
                log.info("비밀번호 변경 성공");
                httpSession.invalidate();
                resultType = "success";
                message = "비밀번호가 변경 되었습니다.";
                nextPage = "redirect:/";
                break;
            case UserService.NOT_MATCH:
                log.info("비밀번호 변경 실패");
                resultType = "fail";
                message = "비밀번호가 일치하지 않습니다.";
                break;
            default:
                log.info("비밀번호 인증 실패");
                resultType = "fail";
                message = "현재 비밀번호가 일치하지 않습니다.";
                break;
        }

        redirectAttributes.addFlashAttribute("resultType", resultType);
        redirectAttributes.addFlashAttribute("message", message);
        return nextPage;

    }

    @GetMapping("/findPw_Auth")
    public String findpw_auth(){
        return "/user/findPw_Auth";
    }

    @PostMapping("/findPw_Auth")
    public String findPwAuth(@RequestParam("name") String name, @RequestParam("email") String email, RedirectAttributes redirectAttributes,Model model) {

        int result = userService.SetAndSendingPW(email,name);


        String nextPage = "redirect:/users/findPw_Auth";
        switch (result) {
            case UserService.SUCCESS:
                log.info("메일 전송 성공");
                resultType = "success";
                message = "임시 메일이 전송되었습니다.";
                nextPage = "redirect:/";
                break;
            case UserService.NOT_MATCH:
                log.info("회원 정보 에러");
                resultType = "fail";
                message = "회원 정보가 다릅니다.";
                break;
        }

        redirectAttributes.addFlashAttribute("resultType", resultType);
        redirectAttributes.addFlashAttribute("message", message);
        return nextPage;

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
        return "redirect:/users/profile/" + followerId;
    }

    // 언팔로우
    @PostMapping("/{followerId}/unfollow/{followingId}")
    public String unFollowUser(@PathVariable Long followerId, @PathVariable Long followingId) {
        userService.unFollowUser(followerId, followingId);
        return "redirect:/users/profile/" + followerId;
    }

    // 팔로워 목록
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

    // 팔로잉 목록
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

    // 코멘트 내역
    @GetMapping("/profile/{id}/comments")
    public String showComments(@PathVariable Long id, Model model, HttpSession session) {
        List<CommentResponse> comments = commentService.getComments(id);

        model.addAttribute("comments", comments);
        model.addAttribute("profileUserId", id);
        return "/user/comments";
    }

    // 위시리스트 내역
    @GetMapping("/profile/{id}/wish")
    public String showWish(@PathVariable Long id, Model model) {
        List<WishWatchingResponse> wishWatchingResponses = wishWatchingService.getWishResponse(id);

        model.addAttribute("wishwatchs", wishWatchingResponses);
        model.addAttribute("profileUserId", id);
        return "/user/wish";
    }

    // 보는 중 리스트 내역
    @GetMapping("/profile/{id}/watching")
    public String showWatching(@PathVariable Long id, Model model) {
        List<WishWatchingResponse> wishWatchingResponses = wishWatchingService.getWatchingResponse(id);

        model.addAttribute("wishwatchs", wishWatchingResponses);
        model.addAttribute("profileUserId", id);
        return "/user/watching";
    }
}
