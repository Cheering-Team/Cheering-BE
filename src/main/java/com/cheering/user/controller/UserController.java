package com.cheering.user.controller;

import com.cheering.common.dto.ResponseBodyDto;
import com.cheering.common.dto.ResponseGenerator;
import com.cheering.user.domain.User;
import com.cheering.user.dto.SignUpRequest;
import com.cheering.user.dto.SignUpResponse;
import com.cheering.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseBodyDto<?>> signUp(@RequestBody SignUpRequest signUpRequest) {
        User joinUser = userService.signup(signUpRequest);

        SignUpResponse userResponse = new SignUpResponse(joinUser.getId());

        //accessToken 생성 로직
        String accessToken = "accessTokenString";
        
        return ResponseGenerator.success(
                200, accessToken, "signup success", userResponse);
    }

    @GetMapping("/signin")
    public String signIn() {
        return "signin complete";
    }
}
