package com.pedia.movie.movie.service;

import com.pedia.movie.movie.dto.*;
import com.pedia.movie.movie.entity.Film;
import com.pedia.movie.movie.entity.FilmImg;
import com.pedia.movie.movie.entity.FilmVideo;
import com.pedia.movie.movie.repository.FilmImgRepository;
import com.pedia.movie.movie.repository.FilmRepository;
import com.pedia.movie.movie.repository.FilmVideoRepository;
import com.pedia.movie.user.dto.WishWatchingResponse;
import com.pedia.movie.user.dto.CommentResponse;
import com.pedia.movie.user.entity.Rating;
import com.pedia.movie.user.entity.WishWatchList;
import com.pedia.movie.user.repository.RatingRepository;
import com.pedia.movie.user.repository.WishWatchRepository;
import com.pedia.movie.user.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class FilmService {

    private final FilmRepository filmRepository;
    private final FilmImgRepository filmImgRepository;
    private final FilmVideoRepository filmVideoRepository;
    private final RatingRepository ratingRepository;
    private final WishWatchRepository wishWatchRepository;
    private final CommentService commentService;
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

    @Transactional
    public List<DailyAndWeeklyResponse> getDailyBoxOffice(String targetDate) {
        String url = KOBIS_API_URL + "?key=" + KOBIS_API_KEY + "&targetDt=" + targetDate;
        DailyBoxOfficeResponse dailyBoxOfficeResponse = restTemplate.getForObject(url, DailyBoxOfficeResponse.class);
        log.info("dailyBoxOfficeResponse: {}", dailyBoxOfficeResponse);

        List<DailyAndWeeklyResponse> dailyResponses = new ArrayList<>();
        if (dailyBoxOfficeResponse != null && dailyBoxOfficeResponse.getBoxOfficeResult() != null) {
            for (DailyBoxOfficeResponse.DailyBoxOfficeMovie dailyBoxOfficeMovie : dailyBoxOfficeResponse.getBoxOfficeResult().getDailyBoxOfficeList()) {
                Long movieCd = Long.parseLong(dailyBoxOfficeMovie.getMovieCd());
                Film film = getOrCreateFilm(movieCd, dailyBoxOfficeMovie.getMovieNm(), null);
                log.info("film: {}", film);

                if (film != null) {
                    DailyAndWeeklyResponse dailyAndWeeklyResponse = new DailyAndWeeklyResponse();
                    dailyAndWeeklyResponse.setId(film.getId());
                    dailyAndWeeklyResponse.setTitle(film.getTitle());
                    dailyAndWeeklyResponse.setPosterPath(film.getPosterPath());
                    dailyAndWeeklyResponse.setRank(Integer.parseInt(dailyBoxOfficeMovie.getRank()));
                    dailyAndWeeklyResponse.setAudiAcc(Integer.parseInt(dailyBoxOfficeMovie.getAudiAcc()));
                    dailyAndWeeklyResponse.setReleaseDate(film.getReleaseDate());
                    dailyAndWeeklyResponse.setAverageRating(film.getAverageRating());

                    dailyResponses.add(dailyAndWeeklyResponse);
                }
            }
        }
        log.info("dailyResponses: {}", dailyResponses);
        return dailyResponses;
    }

    @Transactional
    public List<DailyAndWeeklyResponse> getWeeklyBoxOffice(String targetDate) {
        String url = KOBIS_API_WEEK_URL + "?key=" + KOBIS_API_KEY + "&targetDt=" + targetDate;
        System.out.println(url);
        WeeklyBoxOfficeResponse weeklyBoxOfficeResponse = restTemplate.getForObject(url, WeeklyBoxOfficeResponse.class);
        log.info("WeeklyBoxOfficeResponse: {}", weeklyBoxOfficeResponse);

        List<DailyAndWeeklyResponse> weeklyResponses = new ArrayList<>();
        if (weeklyBoxOfficeResponse != null && weeklyBoxOfficeResponse.getBoxOfficeResult() != null) {
            for (WeeklyBoxOfficeResponse.WeeklyBoxOfficeMovie weeklyBoxOfficeMovie : weeklyBoxOfficeResponse.getBoxOfficeResult().getWeeklyBoxOfficeList()) {
                Long movieCd = Long.parseLong(weeklyBoxOfficeMovie.getMovieCd());
                Film film = getOrCreateFilm(movieCd, weeklyBoxOfficeMovie.getMovieNm(), null);
                log.info("film: {}", film);

                if (film != null) {
                    DailyAndWeeklyResponse dailyAndWeeklyResponse = new DailyAndWeeklyResponse();
                    dailyAndWeeklyResponse.setId(film.getId());
                    dailyAndWeeklyResponse.setTitle(film.getTitle());
                    dailyAndWeeklyResponse.setPosterPath(film.getPosterPath());
                    dailyAndWeeklyResponse.setRank(Integer.parseInt(weeklyBoxOfficeMovie.getRank()));
                    dailyAndWeeklyResponse.setAudiAcc(Integer.parseInt(weeklyBoxOfficeMovie.getAudiAcc()));
                    dailyAndWeeklyResponse.setReleaseDate(film.getReleaseDate());
                    dailyAndWeeklyResponse.setAverageRating(film.getAverageRating());

                    weeklyResponses.add(dailyAndWeeklyResponse);
                }
            }
        }
        log.info("weeklyResponses: {}", weeklyResponses);
        return weeklyResponses;
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
            WishWatchList wishWatchList = wishWatchRepository.findByUserIdAndFilmId(userId, filmId);
            if (rating != null) {
                response.setUserScore(rating.getScore());
            }
            if (wishWatchList != null) {
                WishWatchingResponse wishWatchingResponse = new WishWatchingResponse();
                wishWatchingResponse.setWish(wishWatchList.isWish());
                wishWatchingResponse.setWatching(wishWatchList.isWatch());
                response.setWishWatchingResponse(wishWatchingResponse);
            }
        }

        return response;
    }

    @Transactional
    public List<UpcomingBoxOfficeResponse> getUpcomingFilmList() {
        LocalDate minDate = LocalDate.now().plusDays(1);
        String url = "https://api.themoviedb.org/3/movie/upcoming?api_key=" + TMDB_API_KEY + "&language=ko-KR&page=1&region=kr"
                + "&release_date.gte=" + minDate;
        System.out.println(url);
        UpcomingResponse upcomingResponse = restTemplate.getForObject(url, UpcomingResponse.class);
        log.info("upcomingResponse: {}", upcomingResponse);

        List<UpcomingBoxOfficeResponse> films = new ArrayList<>();
        if(upcomingResponse != null && upcomingResponse.getResults() != null) {
            List<UpcomingResponse.Movie> allMovies = upcomingResponse.getResults();
            int count = Math.min(allMovies.size(), 10);
            int start = 0;
            LocalDate today = LocalDate.now();
            while(start < count) {
                UpcomingResponse.Movie movie = allMovies.get(start);
                LocalDate releaseDate = LocalDate.parse(movie.getReleaseDate());
                if(releaseDate.isBefore(minDate)) {
                    start++;
                    count++;
                    continue;
                }
                Film film = getOrCreateFilm(null, movie.getTitle(), movie.getId());
                if(film != null) {
                    UpcomingBoxOfficeResponse upcomingBoxOfficeResponse = new UpcomingBoxOfficeResponse();
                    upcomingBoxOfficeResponse.setId(film.getId());
                    upcomingBoxOfficeResponse.setTitle(film.getTitle());
                    upcomingBoxOfficeResponse.setPosterPath(film.getPosterPath());
                    upcomingBoxOfficeResponse.setReleaseDate(film.getReleaseDate());
                    upcomingBoxOfficeResponse.setAverageRating(film.getAverageRating());

                    long daysUntil = ChronoUnit.DAYS.between(today, releaseDate);
                    upcomingBoxOfficeResponse.setDaysUntil(daysUntil);

                    films.add(upcomingBoxOfficeResponse);
                }
                start++;
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

    private Film getOrCreateFilm(Long movieCd, String title, Long movieId) {
        Film film = null;

        if (movieCd != null) {
            film = filmRepository.findByMovieCd(movieCd);
        }

        if (film == null && movieId != null) {
            film = filmRepository.findByMovieId(movieId);
        }

        if (film == null) {
            film = filmRepository.findByTitle(title);
        }

        if (film == null) {
            film = getMovieFromTMDB(title);
            if (film != null) {
                if (movieCd != null) film.setMovieCd(movieCd);
                if (movieId != null) film.setMovieId(movieId);
                filmRepository.save(film);
            }
        } else {
            boolean updated = false;
            if (movieCd != null && film.getMovieCd() == null) {
                film.setMovieCd(movieCd);
                updated = true;
            }
            if (movieId != null && film.getMovieId() == null) {
                film.setMovieId(movieId);
                updated = true;
            }
            if (updated) {
                filmRepository.save(film);
            }
        }

        return film;
    }

    public List<CommentResponse> getCommentListByFilmId(Long id) {
        return commentService.getCommentsByFilmId(id);
    }
}


