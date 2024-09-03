package com.pedia.movie.user.repository;

import com.pedia.movie.user.entity.Follow;
import com.pedia.movie.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);
    List<Follow> findByFollowing(User following);
    List<Follow> findByFollower(User follower);
}
