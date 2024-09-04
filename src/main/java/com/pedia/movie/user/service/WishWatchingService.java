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
            }else {
                wishWatchList.setWish(false);
                wishWatchList.setWatch(true);

            }
            wishWatchList.setUser(user);
            wishWatchList.setFilm(film);
            wishWatchRepository.save(wishWatchList);
        } else {
            //엔티티값이 널이 아닌경우
            if(action.equals("wish")){
                if(wishWatchList.isWish()){
                    wishWatchRepository.delete(wishWatchList);
                    return null;
                }else{
                    wishWatchList.setWish(true);
                    wishWatchList.setWatch(false);
                }
            }else{
                if(wishWatchList.isWatch()){
                    wishWatchRepository.delete(wishWatchList);
                    return null;
                }else{
                    wishWatchList.setWish(false);
                    wishWatchList.setWatch(true);

                }
            }

        }
        WishWatchingResponse wishWatchingResponse = new WishWatchingResponse();
        wishWatchingResponse.setWish(wishWatchList.isWish());
        wishWatchingResponse.setWatching(wishWatchList.isWatch());
        return wishWatchingResponse;
    }
}
