package com.cheering.user.service;

import com.cheering.user.domain.User;
import com.cheering.user.domain.UserRepository;
import com.cheering.user.dto.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

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
}
