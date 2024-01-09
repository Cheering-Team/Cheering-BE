package com.cheering.user.service;

import com.cheering.global.exception.user.DuplicatedEmailException;
import com.cheering.global.exception.user.InvalidEmailFormatException;
import com.cheering.user.domain.User;
import com.cheering.user.domain.UserRepository;
import com.cheering.user.dto.SignUpRequest;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final static String REGEXP_EMAIL = "^[A-Za-z0-9_.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]*\\.*[A-Za-z0-9\\-]+$";
    @Autowired
    UserRepository userRepository;

    @Transactional
    public User signup(SignUpRequest signUpRequest) {
        User newUser = User.builder()
                .email(signUpRequest.email())
                .password(signUpRequest.password())
                .nickname(signUpRequest.nickname())
                .build();

        userRepository.save(newUser);

        return newUser;
    }

    public void login() {

    }

    public void validateEmailFormat(String email) {
        //이메일 형식 검사 -> throw
        if (!Pattern.matches(REGEXP_EMAIL, email)) {
            throw new InvalidEmailFormatException();
        }
    }

    public void validateDuplicatedEmail(String email) {
        // 기존 이메일과 중복 검사

        if (userRepository.existsUserByEmail(email)) {
            throw new DuplicatedEmailException();
        }
    }
}
