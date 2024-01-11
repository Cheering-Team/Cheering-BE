package com.cheering.user.service;

import com.cheering.auth.constant.Role;
import com.cheering.global.exception.constant.ExceptionMessage;
import com.cheering.global.exception.user.DuplicatedEmailException;
import com.cheering.global.exception.user.InvalidEmailFormatException;
import com.cheering.global.exception.user.MisMatchPasswordException;
import com.cheering.global.exception.user.NotFoundUserException;
import com.cheering.user.domain.User;
import com.cheering.user.domain.UserRepository;
import com.cheering.user.dto.SignInRequest;
import com.cheering.user.dto.SignUpRequest;
import java.util.Optional;
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
    public User signUp(SignUpRequest signUpRequest) {
        User newUser = User.builder()
                .email(signUpRequest.email())
                .password(signUpRequest.password())
                .nickname("")
                .role(Role.ROLE_USER)
                .build();

        return userRepository.save(newUser);
    }

    public User signIn(SignInRequest signInRequest) {

        String email = signInRequest.email();
        String password = signInRequest.password();

        Optional<User> findUser = userRepository.findByEmailAndPassword(email, password);
        return findUser.orElseThrow(() ->
                new NotFoundUserException(ExceptionMessage.NOT_FOUND_USER));
    }

    public void validateEmailFormat(String email) {
        //이메일 형식 검사 -> throw
        if (!Pattern.matches(REGEXP_EMAIL, email)) {
            throw new InvalidEmailFormatException(ExceptionMessage.INVALID_EMAIL_FORMAT);
        }
    }

    public void validateDuplicatedEmail(String email) {
        // 기존 이메일과 중복 검사
        if (userRepository.existsUserByEmail(email)) {
            throw new DuplicatedEmailException(ExceptionMessage.DUPLICATED_EMAIL);
        }
    }

    public void validateConfirmPassword(String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            throw new MisMatchPasswordException(ExceptionMessage.INVALID_EMAIL_FORMAT);
        }
    }
}
