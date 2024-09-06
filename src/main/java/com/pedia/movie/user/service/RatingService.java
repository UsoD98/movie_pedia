package com.pedia.movie.user.service;

import com.pedia.movie.movie.entity.Film;
import com.pedia.movie.movie.repository.FilmRepository;
import com.pedia.movie.user.dto.RatingResponse;
import com.pedia.movie.user.entity.Rating;
import com.pedia.movie.user.entity.User;
import com.pedia.movie.user.repository.RatingRepository;
import com.pedia.movie.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {

    final static public int INSERTED = 1;
    final static public int DELETED = -1;
    final static public int UPDATED = 0;

    private final RatingRepository ratingRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

    @Transactional
    public int rateFilm(Long userId, Long filmId, Double score) {
        User user = userRepository.findById(userId).orElse(null);
        Film film = filmRepository.findById(filmId).orElse(null);

        Rating rating = ratingRepository.findByUserAndFilm(user, film);
        if (rating == null) {
            // 평가 이력이 없으면 새로 insert
            rating = new Rating();
            rating.setUser(user);
            rating.setFilm(film);
            rating.setScore(score);
            assert user != null;
            user.incrementRatingsCount();
            Rating savedRating = ratingRepository.save(rating);
            createRatingResponse(savedRating);
            return INSERTED;
        } else if (rating.getScore().equals(score)) {
            // 평가 이력이 있고 저장된 score와 입력된 score가 같다면 delete
            assert user != null;
            user.decrementRatingsCount();
            ratingRepository.delete(rating);
            return DELETED;
        } else {
            // 평가 이력이 있고 저장된 score와 입력된 score가 다르다면 update
            rating.setScore(score);
            Rating updatedRating = ratingRepository.save(rating);
            createRatingResponse(updatedRating);
            return UPDATED;
        }
    }

    // 평점 단건 조회
    @Transactional
    public RatingResponse getRating(Long userId, Long filmId) {
        User user = userRepository.findById(userId).orElse(null);
        Film film = filmRepository.findById(filmId).orElse(null);

        Rating rating = ratingRepository.findByUserAndFilm(user, film);

        if (rating == null) {
            return null;
        }

        return createRatingResponse(rating);
    }

    // 한 유저에 대한 모든 평점 조회
    @Transactional
    public List<RatingResponse> getRatings(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        List<Rating> ratings = ratingRepository.findAllByUser(user);

        return ratingRepository.findAllByUserIdWithFilm(userId).stream()
                .map(this::createRatingResponse)
                .collect(Collectors.toList());
    }

    // 평점 조회 시 필요한 정보만 반환
    private RatingResponse createRatingResponse(Rating rating) {
        RatingResponse ratingResponse = new RatingResponse();
        ratingResponse.setScore(rating.getScore());
        ratingResponse.setFilmId(rating.getFilm().getId());
        ratingResponse.setFilmTitle(rating.getFilm().getTitle());
        ratingResponse.setFilmPosterPath(rating.getFilm().getPosterPath());
        return ratingResponse;
    }

    public Long getRatingCount() {
        return ratingRepository.countAllRatings();
    }
}
