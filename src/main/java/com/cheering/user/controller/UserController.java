package com.cheering.user.controller;

import com.cheering.auth.jwt.JWToken;
import com.cheering.auth.jwt.JwtGenerator;
import com.cheering.global.dto.ResponseBodyDto;
import com.cheering.global.dto.ResponseGenerator;
import com.cheering.user.domain.User;
import com.cheering.user.dto.SignUpRequest;
import com.cheering.user.dto.SignUpResponse;
import com.cheering.user.service.UserService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final JwtGenerator jwtGenerator;

    @PostMapping("/signup")
    public ResponseEntity<ResponseBodyDto<?>> signUp(@RequestBody SignUpRequest signUpRequest) {
        User joinUser = userService.signup(signUpRequest);

        //jwt 토큰 생성
        JWToken jwToken = jwtGenerator.generateToken(joinUser.getId());

        SignUpResponse signUpResponse = new SignUpResponse(joinUser.getId());
        return ResponseGenerator.success(
                200, jwToken.accessToken(), "signup success", signUpResponse);
    }

    @GetMapping("/signin")
    public String signIn() {
        return "signin complete";
    }

    @PostMapping("/email")
    public ResponseEntity<ResponseBodyDto<?>> validateEmail(@RequestBody Map<String, String> data) {
        String email = data.get("email");

        userService.validateEmail(email);

        return ResponseGenerator.success(
                200, "signup success", null);
    }
}
