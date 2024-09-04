package com.pedia.movie.movie.service;

import com.pedia.movie.movie.dto.*;
import com.pedia.movie.movie.entity.Film;
import com.pedia.movie.movie.entity.FilmImg;
import com.pedia.movie.movie.entity.FilmVideo;
import com.pedia.movie.movie.repository.FilmImgRepository;
import com.pedia.movie.movie.repository.FilmRepository;
import com.pedia.movie.movie.repository.FilmVideoRepository;
import com.pedia.movie.user.entity.Rating;
import com.pedia.movie.user.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class FilmService {

    private final FilmRepository filmRepository;
    private final FilmImgRepository filmImgRepository;
    private final FilmVideoRepository filmVideoRepository;
    private final RatingRepository ratingRepository;

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

    public FilmDetailResponse getFilmDetail(Long filmId, Long userId) {
        Film film = filmRepository.findById(filmId).orElse(null);
        if (film == null) {
            return null;
        }

        FilmDetailResponse response = FilmDetailResponse.from(film);
        response.setRatingCount(film.getRatingCount());
        response.setAverageRating(film.getAverageRating());

        if (userId != null) {
            Rating rating = ratingRepository.findByUserIdAndFilmId(userId, filmId);
            if (rating != null) {
                response.setUserScore(rating.getScore());
            }
        }

        return response;
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
                if(LocalDate.parse(upcomingResponse.getResults().get(start).getReleaseDate()).isBefore(minDate)){
                    start++;
                    count++;
                    continue;
                }
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

    public List<FilmImg> getFilmDetailImg(Long movieId) {
        Film film = filmRepository.findByMovieId(movieId);
        List<FilmImg> filmImgSet = filmImgRepository.findByFilm(film);
        if (filmImgSet != null && !filmImgSet.isEmpty()) {
            return filmImgSet;
        } else {
            String url = "https://api.themoviedb.org/3/movie/" + movieId + "/images?api_key=" + TMDB_API_KEY;
            FilmImgResponse filmImgResponse = restTemplate.getForObject(url, FilmImgResponse.class);
            log.info("FilmImgResponse: {}", filmImgResponse);

            //가져온 데이터가 널이 아닐경우
            if (filmImgResponse != null) {
                filmImgSet = new ArrayList<FilmImg>();
                int count = Math.min(filmImgResponse.getBackdrops().size(), 10);
                for (int i = 0; i < count; i++) {
                    FilmImg filmImg = new FilmImg();
                    filmImg.setFilePath(filmImgResponse.getBackdrops().get(i).getFilePath());
                    filmImg.setWidth(filmImgResponse.getBackdrops().get(i).getWidth());
                    filmImg.setHeight(filmImgResponse.getBackdrops().get(i).getHeight());
                    filmImg.setFilm(film);
                    filmImgRepository.save(filmImg);
                    filmImgSet.add(filmImg);
                }
                return filmImgSet;
            }
            return null;
        }

    }
        public List<FilmVideo> getFilmDetailVideo(Long movieId) {

            Film film = filmRepository.findByMovieId(movieId);
            List<FilmVideo> filmVideoSet = filmVideoRepository.findByFilm(film);
            if(filmVideoSet != null && !filmVideoSet.isEmpty()){
                return filmVideoSet;
            }else{
                String url = "https://api.themoviedb.org/3/movie/"+ movieId +"/videos"+"?api_key=" + TMDB_API_KEY;
                FilmVideoResponse filmVideoResponse = restTemplate.getForObject(url, FilmVideoResponse.class);
                log.info("FilmVideoResponse: {}", filmVideoResponse);

                //가져온 데이터가 널이 아닐경우
                if(filmVideoResponse != null) {
                    filmVideoSet = new ArrayList<FilmVideo>();
                    int count = Math.min(filmVideoResponse.getResults().size(),10);
                    for (int i = 0; i < count; i++) {
                        FilmVideo filmVideo = new FilmVideo();
                        filmVideo.setFilm(film);
                        filmVideo.setVideoKey(filmVideoResponse.getResults().get(i).getKey());
                        filmVideo.setVideoName(filmVideoResponse.getResults().get(i).getName());
                        filmVideoRepository.save(filmVideo);
                        filmVideoSet.add(filmVideo);
                    }
                    return filmVideoSet;
                }
                return null;
            }
    }
}


