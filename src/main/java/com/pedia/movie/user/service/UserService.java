package com.pedia.movie.user.service;

import com.pedia.movie.config.MailConfig;
import com.pedia.movie.user.dto.CommentResponse;
import com.pedia.movie.user.dto.UserResponse;
import com.pedia.movie.user.entity.Follow;
import com.pedia.movie.user.entity.User;
import com.pedia.movie.user.repository.FollowRepository;
import com.pedia.movie.user.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Date;
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
    private final CommentService commentService;
    private MailConfig mailConfig;

    private final JavaMailSender javaMailSender; // JavaMailSender 인터페이스 사용



    // 회원가입
    @Transactional
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
    @Transactional
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
    @Transactional
    public Long findIdByEmail(String email) {
        return userRepository.findIdByEmail(email);
    }

    // 유저 정보 가져오기
    @Transactional
    public UserResponse findUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }
        return UserResponse.from(user);
    }

    //유저 정보 가져오기 네임 메일
    @Transactional
    public UserResponse findUserByNameAndEmail(String name, String email){
        User user = userRepository.findByNameAndEmail(name,email);
        if( user == null){
            return null;
        }
        return UserResponse.from(user);
    }

    public String createNewPassword(){
        System.out.println("createNewPassword()");
        char[] chars = new char[] {
                '0','1','2','3','4','5','6','7','8','9',
                'a','b','c','d','e','f','g','h','i', 'j',
                'k','l','m','n','o','p','q','r','s','t',
                'u','v','w','x','y','z'
        };

        StringBuffer stringBuffer = new StringBuffer();
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(new Date().getTime());

        int index=0;
        int length = chars.length;

        for(int i = 0 ;i<8;i++){
            index = secureRandom.nextInt(length);

            if(index % 2 == 0)
                stringBuffer.append(String.valueOf(chars[index]).toUpperCase());
            else
                stringBuffer.append(String.valueOf(chars[index]).toLowerCase());
        }
        System.out.println("NEW PASSWORD: " + stringBuffer.toString());

        return stringBuffer.toString();
    }

    private void sendNewPasswordByMail(String toMailAddr, String newPassword){
        System.out.println("sendNewPasswordByMail()");
        final MimeMessagePreparator mimeMessagePreparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                mimeMessageHelper.setTo(toMailAddr);
                mimeMessageHelper.setSubject("왓챠피디아 새 비밀번호 안내입니다.");
                mimeMessageHelper.setText("새 비밀번호: " + newPassword, true);
            }
        };
        javaMailSender.send(mimeMessagePreparator); // JavaMailSender 사용

    }

    // 유저 팔로우
    @Transactional
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
    @Transactional
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
    @Transactional
    public List<UserResponse> getFollowers(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(null);
        return followRepository.findByFollowing(user).stream()
                .map(follow -> UserResponse.from(follow.getFollower()))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<UserResponse> getFollowings(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(null);
        return followRepository.findByFollower(user).stream()
                .map(follow -> UserResponse.from(follow.getFollowing()))
                .collect(Collectors.toList());
    }

    // 코멘트 확인
    public List<CommentResponse> getComments(Long userId) {
        return commentService.getComments(userId);
    }

    public int SetAndSendingPW( String email, String name) {

        User user = userRepository.findByNameAndEmail(name,email);
        if (user == null) {
            return -2;
        }

        String tempPW = createNewPassword();
        user.setPassword(tempPW);
        userRepository.save(user);

        sendNewPasswordByMail(email,tempPW);
        return 1;
    }

    public int modifyPW(Long userId, String password, String newPassword, String newPasswordCheck) {
        if(!newPassword.equals(newPasswordCheck)){
            return -2;
        }
        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            return -2;
        }
        if(!password.equals(user.getPassword())){
            return -1;
        }
        user.setPassword(newPassword);
        userRepository.save(user);

        return 1;
    }
}