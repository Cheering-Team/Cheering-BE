package com.cheering.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.cheering.global.exception.user.DuplicatedEmailException;
import com.cheering.global.exception.user.NotFoundUserException;
import com.cheering.user.domain.User;
import com.cheering.user.domain.repository.UserRepository;
import com.cheering.user.dto.request.SignInRequest;
import com.cheering.user.dto.request.SignUpRequest;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class UserServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;

    @Test
    @Transactional
    void 회원가입_성공_테스트() {
        //given
        SignUpRequest signUpUser = new SignUpRequest("cheering@naver.com", "123456789", "123456789", "nickName");

        //when
        User signup = userService.signUp(signUpUser);
        Optional<User> findUser = userRepository.findById(signup.getId());

        //then
        assertThatCode(() -> {
            User user = findUser.orElseThrow();

        }).doesNotThrowAnyException();
    }

    @Test
    @Transactional
    void 로그인_성공_테스트() {
        //given
        SignUpRequest signUpUserDto = new SignUpRequest("cheering@naver.com", "123456789", "123456789", "nickName");
        User signUpUser = userService.signUp(signUpUserDto);
        SignInRequest signInRequest = new SignInRequest("cheering@naver.com", "123456789");

        //when
        User loginUser = userService.signIn(signInRequest);

        //then
        assertThat(signUpUser.getId()).isEqualTo(loginUser.getId());
    }

    @Test
    @Transactional
    void 로그인_실패_테스트() {
        //given
        SignUpRequest signUpUserDto = new SignUpRequest("cheering@naver.com", "123456789", "123456789", "nickName");
        User signUpUser = userService.signUp(signUpUserDto);
        SignInRequest signInRequest = new SignInRequest("cheering@naver.com", "wrongPassword");

        //when
        //then
        assertThatThrownBy(() -> {
            User loginUser = userService.signIn(signInRequest);
        }).isInstanceOf(NotFoundUserException.class);


    }

    @ParameterizedTest
    @ValueSource(strings = {"cheering@konkuk.ac.kr", "cheering@google.com"})
    void 이메일_검증_성공_테스트(String rightEmail) {
        //given

        //when
        //then
        assertThatCode(() -> {
            userService.validateEmailFormat(rightEmail);
            userService.validateDuplicatedEmail(rightEmail);
        }).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"cheeringNaver.com", "cheering@navercom", "cheering@naver.ac.kr.kr"})
    void 이메일_유효성_예외_테스트(String wrongEmail) {
        assertThatThrownBy(() -> {
            userService.validateEmailFormat(wrongEmail);
        });
    }

    @Test
    @Transactional
    void 이메일_중복_예외_테스트() {
        //given
        SignUpRequest signUpUser = new SignUpRequest("cheering@naver.com", "123456789", "123456789", "nickName");
        User signup = userService.signUp(signUpUser);

        //when
        String newEmail = "cheering@naver.com";

        //then
        assertThatThrownBy(() -> {
            userService.validateDuplicatedEmail(newEmail);
        }).isInstanceOf(DuplicatedEmailException.class);
    }

    @Test
    void 비밀번호_일치_성공_테스트() {
        //given
        String password = "aaa111!!!";
        String passwordConfirm = "aaa111!!!";
        //when
        //then
        assertThat(password).isEqualTo(passwordConfirm);
    }

    @Test
    void 비밀번호_일치_실패_테스트() {
        //given
        String password = "aaa111!!!";
        String passwordConfirm = "aaa111!!!aa";
        //when
        //then
        assertThat(password).isNotEqualTo(passwordConfirm);
    }
}