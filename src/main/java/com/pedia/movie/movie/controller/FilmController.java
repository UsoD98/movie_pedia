package com.pedia.movie.movie.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedia.movie.movie.dto.FilmDetailResponse;
import com.pedia.movie.movie.entity.Film;
import com.pedia.movie.movie.service.FilmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private final ObjectMapper objectMapper;

    @GetMapping("/films")
    public String getFilms(Model model) {
        String targetDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);
        List<Film> dailyBoxOffice = filmService.getDailyBoxOffice(targetDate);
        try {
            log.info("Daily Box Office data: {}", objectMapper.writeValueAsString(dailyBoxOffice));
        } catch (Exception e) {
            log.error("Error logging daily box office data", e);
        }

        model.addAttribute("dailyBoxOffice", dailyBoxOffice);

        return "films";
    }

    @GetMapping("/weekly_films")
    public String getWeekFilms(Model model) {
        String targetDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);
        List<Film> weeklyBoxOffice = filmService.getWeeklyBoxOffice(targetDate);
        try {
            log.info("Weekly Box Office data: {}", objectMapper.writeValueAsString(weeklyBoxOffice));
        } catch (Exception e) {
            log.error("Error logging daily box office data", e);
        }

        model.addAttribute("weeklyBoxOffice", weeklyBoxOffice);

        return "weekly_films";
    }

    @GetMapping("/films/{id}")
    public String getFilmDetail(Model model, @PathVariable("id") Long id) {
        FilmDetailResponse filmDetailResponse = filmService.getFilmDetail(id);
        if (filmDetailResponse != null) {
            model.addAttribute("film", filmDetailResponse);
            return "filmDetail";
        } else {
            return "redirect:/films";
        }
    }


}
