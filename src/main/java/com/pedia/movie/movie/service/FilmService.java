package com.pedia.movie.movie.service;

import com.pedia.movie.movie.dto.*;
import com.pedia.movie.movie.entity.Film;
import com.pedia.movie.movie.repository.FilmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmRepository filmRepository;

    private final RestTemplate restTemplate;

    @Value("${kobis.api.url}")
    private String KOBIS_API_URL;

    @Value("${kobis.api.week_url}")
    private String KOBIS_API_WEEK_URL;

    @Value("${kobis.api.key}")
    private String KOBIS_API_KEY;

    @Value("${tmdb.api.url}")
    private String TMDB_API_URL;

    @Value("${tmdb.api.key}")
    private String TMDB_API_KEY;

    public List<Film> getDailyBoxOffice(String targetDate) {
        String url = KOBIS_API_URL + "?key=" + KOBIS_API_KEY + "&targetDt=" + targetDate;
        DailyBoxOfficeResponse dailyBoxOfficeResponse = restTemplate.getForObject(url, DailyBoxOfficeResponse.class);
        log.info("dailyBoxOfficeResponse: {}", dailyBoxOfficeResponse);

        List<Film> films = new ArrayList<>();
        if (dailyBoxOfficeResponse != null && dailyBoxOfficeResponse.getBoxOfficeResult() != null) {
            //daily
            for (DailyBoxOfficeResponse.DailyBoxOfficeMovie dailyBoxOfficeMovie : dailyBoxOfficeResponse.getBoxOfficeResult().getDailyBoxOfficeList()) {
                Film film = filmRepository.findByMovieCd(Long.parseLong(dailyBoxOfficeMovie.getMovieCd()));
                log.info("film: {}", film);
                if (film == null) {
                    log.info("film is null");
                    film = getMovieFromTMDB(dailyBoxOfficeMovie.getMovieNm());
                    if (film != null) {
                        film.setMovieCd(Long.parseLong(dailyBoxOfficeMovie.getMovieCd()));
                        filmRepository.save(film);
//                        log.info("film: {}", film);
                    }
                }
                films.add(film);
            }
        }
        log.info("films: {}", films);
        return films;
    }

    public List<Film> getWeeklyBoxOffice(String targetDate) {
        String url = KOBIS_API_WEEK_URL + "?key=" + KOBIS_API_KEY + "&targetDt=" + targetDate;
        System.out.println(url);
        WeeklyBoxOfficeResponse weeklyBoxOfficeResponse = restTemplate.getForObject(url, WeeklyBoxOfficeResponse.class);
        log.info("WeeklyBoxOfficeResponse: {}", weeklyBoxOfficeResponse);

        List<Film> films = new ArrayList<>();
        if (weeklyBoxOfficeResponse != null && weeklyBoxOfficeResponse.getBoxOfficeResult() != null) {
            //daily
            for (WeeklyBoxOfficeResponse.WeeklyBoxOfficeMovie weeklyBoxOfficeMovie : weeklyBoxOfficeResponse.getBoxOfficeResult().getWeeklyBoxOfficeList()) {
                Film film = filmRepository.findByMovieCd(Long.parseLong(weeklyBoxOfficeMovie.getMovieCd()));
                log.info("film: {}", film);
                if (film == null) {
                    log.info("film is null");
                    film = getMovieFromTMDB(weeklyBoxOfficeMovie.getMovieNm());
                    if (film != null) {
                        film.setMovieCd(Long.parseLong(weeklyBoxOfficeMovie.getMovieCd()));
                        filmRepository.save(film);
//                        log.info("film: {}", film);
                    }
                }
                films.add(film);
            }
        }
        log.info("films: {}", films);
        return films;
    }

    private Film getMovieFromTMDB(String movieName) {
        String url = TMDB_API_URL + "?api_key=" + TMDB_API_KEY + "&query=" + movieName + "&language=ko-KR";
        // 실제 url = https://api.themoviedb.org/3/search/movie?api_key=eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIxNjA4ZDQ2ZmI5ZDNhYjBkYTAwOWRlMTczMjkxYWU0MyIsInN1YiI6IjY2ZDMxODgxMDkwOTY5OTQ2MWI2MGE0ZiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.eUvVWOOSMScMnxyo1-5eY382Ji80IBhxB8gZyKqamHU&query=베테랑&language=ko-KR
        TMDBResponse tmdbResponse = restTemplate.getForObject(url, TMDBResponse.class);
        log.info("tmdbResponse: {}", tmdbResponse);

        if (tmdbResponse != null && !tmdbResponse.getResults().isEmpty()) {
            TMDBResponse.TMDBMovie tmdbMovie = tmdbResponse.getResults().get(0);
            Film film = new Film();
            film.setMovieId(tmdbMovie.getId());
            film.setTitle(tmdbMovie.getTitle());
            film.setReleaseDate(LocalDate.parse(tmdbMovie.getReleaseDate()));
            film.setPosterPath(tmdbMovie.getPosterPath());
            film.setOriginalTitle(tmdbMovie.getOriginalTitle());
            film.setOverview(tmdbMovie.getOverview());
            film.setBackdropPath(tmdbMovie.getBackdropPath());
            log.info("film: {}", film);
            return film;
        }
        return null;
    }

    public FilmDetailResponse getFilmDetail(Long id) {
        Optional<Film> film = filmRepository.findById(id);
        return film.map(FilmDetailResponse::from).orElse(null);
    }


    public List<Film> getUpcomingFilmList(){
        System.out.println(LocalDate.now());
        LocalDate minDate = LocalDate.now().plusDays(1);
        String url = "https://api.themoviedb.org/3/movie/upcoming?api_key=" + TMDB_API_KEY + "&language=ko-KR&page=1&region=kr"
                + "&release_date.gte=" + minDate;
        System.out.println(url);
        UpcomingResponse upcomingResponse = restTemplate.getForObject(url, UpcomingResponse.class);
        log.info("upcomingResponse: {}", upcomingResponse);


        List<Film> films = new ArrayList<>();
        if(upcomingResponse != null && upcomingResponse.getResults()!=null){
            //상위 10개의 영화 추출
            List<UpcomingResponse.Movie> allMovies = upcomingResponse.getResults();
            int count = Math.min(allMovies.size(), 10); //더 작은 것을 채택
            int start = 0;
            while(start < count){
                Film film = filmRepository.findByMovieId((allMovies.get(start).getId()));
                log.info("film: {}",film);
                //만약 데이터 베이스에 없으면
                if(film == null){
                    film = new Film();
                    film.setMovieId(allMovies.get(start).getId());
                    film.setTitle(allMovies.get(start).getTitle());
                    film.setReleaseDate(LocalDate.parse(allMovies.get(start).getReleaseDate()));
                    film.setPosterPath(allMovies.get(start).getPosterPath());
                    film.setOriginalTitle(allMovies.get(start).getOriginalTitle());
                    film.setOverview(allMovies.get(start).getOverview());
                    film.setBackdropPath(allMovies.get(start).getBackdropPath());
                    filmRepository.save(film);
                    films.add(film);
                }else { //데이터베이스에 있으면 그냥 추가
                    films.add(film);
                }
                start ++;
            }

        }
        log.info("films: {}", films);
        return films;
    }

    public List<Film> searchFilmsByTitle(String title) {
        return this.filmRepository.findByTitleContaining(title);
    }

}
