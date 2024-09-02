package com.pedia.movie.user.service;

import com.pedia.movie.user.dto.UserResponse;
import com.pedia.movie.user.entity.User;
import com.pedia.movie.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    final static public int SUCCESS = 1;
    final static public int FAIL = -1;
    final static public int ALREADY_EXIST = 0;
    final static public int NOT_MATCH = -2;

    private final UserRepository userRepository;

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
            return FAIL;
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
}
