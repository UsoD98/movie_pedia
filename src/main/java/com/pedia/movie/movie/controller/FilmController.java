package com.pedia.movie.movie.controller;
import com.pedia.movie.movie.dto.DailyAndWeeklyResponse;
import com.pedia.movie.movie.dto.FilmDetailResponse;
import com.pedia.movie.movie.dto.UpcomingBoxOfficeResponse;
import com.pedia.movie.movie.entity.Film;
import com.pedia.movie.movie.entity.FilmImg;
import com.pedia.movie.movie.entity.FilmVideo;
import com.pedia.movie.movie.service.FilmService;
import com.pedia.movie.user.dto.CommentResponse;
import com.pedia.movie.user.service.CommentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private final CommentService commentService;

    @GetMapping(value = {"/", "/films"})
    public String getFilms(Model model) {
        String targetDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);
        List<DailyAndWeeklyResponse> dailyBoxOffice = filmService.getDailyBoxOffice(targetDate);
        targetDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).format(DateTimeFormatter.BASIC_ISO_DATE);
        List<DailyAndWeeklyResponse> weeklyBoxOffice = filmService.getWeeklyBoxOffice(targetDate);
        List<UpcomingBoxOfficeResponse> upcomingFilms = filmService.getUpcomingFilmList();
        List<CommentResponse> commentLists = commentService.getAllCommentsOrderByDesc();

        model.addAttribute("dailyBoxOffice", dailyBoxOffice);
        model.addAttribute("weeklyBoxOffice", weeklyBoxOffice);
        model.addAttribute("upcomingFilms", upcomingFilms);
        model.addAttribute("comments", commentLists);

        return "films";
    }

    @GetMapping("/weekly_films")
    public String getWeekFilms(Model model) {
        String targetDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).format(DateTimeFormatter.BASIC_ISO_DATE);
        List<DailyAndWeeklyResponse> weeklyBoxOffice = filmService.getWeeklyBoxOffice(targetDate);

        model.addAttribute("weeklyBoxOffice", weeklyBoxOffice);

        return "weekly_films";
    }

    @GetMapping("/films/{id}")
    public String getFilmDetail(Model model, @PathVariable("id") Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        FilmDetailResponse filmDetailResponse = filmService.getFilmDetail(id, userId);
        if (filmDetailResponse != null) {

            List<FilmImg> imgList = filmService.getFilmDetailImg(filmDetailResponse.getMovieId());
            List<FilmVideo> videoList = filmService.getFilmDetailVideo(filmDetailResponse.getMovieId());
            List<CommentResponse> commentList = filmService.getCommentListByFilmId(id);

            model.addAttribute("film_img",imgList);
            model.addAttribute("film_video",videoList);
            model.addAttribute("film", filmDetailResponse);
            model.addAttribute("comments", commentList);
          
            return "filmDetail";
        } else {
            return "redirect:/films";
        }
    }

//    @GetMapping("/upcoming_films")
//    public String getUpcomingFilms(Model model){
//        List<Film> upcomingFilmList= filmService.getUpcomingFilmList();
//        System.out.println("here =========================================");
//
//        model.addAttribute("upcomingFilm",upcomingFilmList);
//        return "upcoming_films";
//    }

    @GetMapping("/films/search")
    public String searchFilms(@RequestParam String title, Model model) {
        List<Film> searchResults = filmService.searchFilmsByTitle(title);
        model.addAttribute("searchResults", searchResults);
        return "search_films";
    }

}
