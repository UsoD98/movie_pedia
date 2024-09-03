package com.pedia.movie.user.service;

import com.pedia.movie.user.dto.UserResponse;
import com.pedia.movie.user.entity.Follow;
import com.pedia.movie.user.entity.User;
import com.pedia.movie.user.repository.FollowRepository;
import com.pedia.movie.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    final static public int SUCCESS = 1;
    final static public int FAIL = -1;
    final static public int ALREADY_EXIST = 0;
    final static public int NOT_MATCH = -2;

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    // 회원가입
    public int registerUser(String name, String email, String password) {

        // 이메일 중복검사
        if (userRepository.existsByEmail(email)) {
            return ALREADY_EXIST;
        }
        try {
            // 중복검사 통과 시 회원가입 처리
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);

            userRepository.save(user);
        } catch (Exception e) {
            return FAIL;
        }
        return SUCCESS;
    }

    // 로그인
    public int login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return NOT_MATCH;
        }
        if (!user.getPassword().equals(password)) {
            return NOT_MATCH;
        }
        return SUCCESS;
    }

    // 로그인 성공 시 유저 정보 가져오기
    public Long findIdByEmail(String email) {
        return userRepository.findIdByEmail(email);
    }

    // 유저 정보 가져오기
    public UserResponse findUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }
        return UserResponse.from(user);
    }

    // 유저 팔로우
    public void followUser(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId).orElse(null);
        User following = userRepository.findById(followingId).orElse(null);

        if (followRepository.findByFollowerAndFollowing(follower, following).isPresent()) {
            return;
        }

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);
        followRepository.save(follow);

        assert follower != null;
        follower.incrementFollowings();
        assert following != null;
        following.incrementFollowers();

        userRepository.save(follower);
        userRepository.save(following);
    }

    // 유저 언팔로우
    public void unFollowUser(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId).orElse(null);
        User following = userRepository.findById(followingId).orElse(null);

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following).orElse(null);

        if (follow == null) {
            return;
        }

        followRepository.delete(follow);

        assert follower != null;
        follower.decrementFollowings();
        assert following != null;
        following.decrementFollowers();

        userRepository.save(follower);
        userRepository.save(following);
    }

    // 팔로우 여부 확인
    public boolean isFollowing(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId).orElse(null);
        User following = userRepository.findById(followingId).orElse(null);
        return followRepository.findByFollowerAndFollowing(follower, following).isPresent();
    }

    // 팔로워 확인
    public List<UserResponse> getFollowers(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(null);
        return followRepository.findByFollowing(user).stream()
                .map(follow -> UserResponse.from(follow.getFollower()))
                .collect(Collectors.toList());
    }

    public List<UserResponse> getFollowings(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(null);
        return followRepository.findByFollower(user).stream()
                .map(follow -> UserResponse.from(follow.getFollowing()))
                .collect(Collectors.toList());
    }
}