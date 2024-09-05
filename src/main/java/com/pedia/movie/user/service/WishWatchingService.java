package com.pedia.movie.user.service;

import com.pedia.movie.movie.entity.Film;
import com.pedia.movie.movie.repository.FilmRepository;
import com.pedia.movie.user.dto.WishWatchingResponse;
import com.pedia.movie.user.entity.User;
import com.pedia.movie.user.entity.WishWatchList;
import com.pedia.movie.user.repository.UserRepository;
import com.pedia.movie.user.repository.WishWatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishWatchingService {
    private final WishWatchRepository wishWatchRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

    @Transactional
    public WishWatchingResponse getWishWatchList(Long userId, Long filmId, String action) {
        User user = userRepository.findById(userId).orElse(null);
        Film film = filmRepository.findById(filmId).orElse(null);

        WishWatchList wishWatchList = wishWatchRepository.findByUserAndFilm(user, film);
        //엔티티 값이 널인 경우
        if (wishWatchList == null) {
            wishWatchList = new WishWatchList();

            if(action.equals("wish")){
                wishWatchList.setWish(true);
                wishWatchList.setWatch(false);
                user.incrementWishCount();
            }else {
                wishWatchList.setWish(false);
                wishWatchList.setWatch(true);
                user.incrementWatchingCount();

            }
            wishWatchList.setUser(user);
            wishWatchList.setFilm(film);
            wishWatchRepository.save(wishWatchList);
        } else {
            //엔티티값이 널이 아닌경우
            if(action.equals("wish")){
                if(wishWatchList.isWish()){
                    wishWatchRepository.delete(wishWatchList);
                    user.decrementWishCount();
                    return null;
                }else{
                    wishWatchList.setWish(true);
                    wishWatchList.setWatch(false);
                    user.incrementWishCount();
                    user.decrementWatchingCount();
                }
            }else{
                if(wishWatchList.isWatch()){
                    wishWatchRepository.delete(wishWatchList);
                    user.decrementWatchingCount();
                    return null;
                }else{
                    wishWatchList.setWish(false);
                    wishWatchList.setWatch(true);
                    user.decrementWishCount();
                    user.incrementWatchingCount();
                }
            }

        }
        WishWatchingResponse wishWatchingResponse = new WishWatchingResponse();
        wishWatchingResponse.setWish(wishWatchList.isWish());
        wishWatchingResponse.setWatching(wishWatchList.isWatch());
        return wishWatchingResponse;
    }


    @Transactional(readOnly = true)
    public List<WishWatchingResponse> showWishWatchingResponse(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        List<WishWatchList> wishWatchLists = wishWatchRepository.findAllByUserId(userId);

        return wishWatchLists.stream()
                .map(this::createWishWatchingResponse)
                .collect(Collectors.toList());
    }

    private WishWatchingResponse createWishWatchingResponse(WishWatchList watchList ) {

        WishWatchingResponse wishWatchingResponse = new WishWatchingResponse();
        wishWatchingResponse.setFilmId(watchList.getFilm().getId());
        wishWatchingResponse.setFilmTitle(watchList.getFilm().getTitle());
        wishWatchingResponse.setWish(watchList.isWish());
        wishWatchingResponse.setWatching(watchList.isWatch());
        wishWatchingResponse.setFilmPosterPath(watchList.getFilm().getPosterPath());

        return wishWatchingResponse;
    }

    public List<WishWatchingResponse> getWishResponse(Long id) {
        List<WishWatchingResponse> wishWatchingResponses = new ArrayList<>();
        for(WishWatchingResponse wishWatchingResponse : showWishWatchingResponse(id)){
            if(wishWatchingResponse.isWish()){
                wishWatchingResponses.add(wishWatchingResponse);
            }
        }
        return wishWatchingResponses;
    }

    public List<WishWatchingResponse> getWatchingResponse(Long id) {
        List<WishWatchingResponse> wishWatchingResponses = new ArrayList<>();
        for(WishWatchingResponse wishWatchingResponse : showWishWatchingResponse(id)){
            if(wishWatchingResponse.isWatching()){
                wishWatchingResponses.add(wishWatchingResponse);
            }
        }
        return wishWatchingResponses;
    }
}
