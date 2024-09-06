package com.pedia.movie.user.repository;

import com.pedia.movie.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 중복검사
    boolean existsByEmail(String email);

    // 이메일로 회원정보 가져오기
    User findByEmail(String email);

    // 비밀번호 검증

    User findByNameAndEmail(String name, String email);

    // 이메일로 유저 아이디 가져오기
    @Query("select u.id from User u where u.email = :email")
    Long findIdByEmail(String email);

}
