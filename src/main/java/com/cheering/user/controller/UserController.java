package com.cheering.user.controller;

import static com.cheering.global.constant.SuccessMessage.SIGN_IN_SUCCESS;
import static com.cheering.global.constant.SuccessMessage.SIGN_UP_SUCCESS;
import static com.cheering.global.constant.SuccessMessage.VALIDATE_EMAIL_SUCCESS;

import com.cheering.auth.jwt.JWToken;
import com.cheering.auth.jwt.JwtGenerator;
import com.cheering.global.constant.Role;
import com.cheering.global.dto.ResponseBodyDto;
import com.cheering.global.dto.ResponseGenerator;
import com.cheering.user.domain.User;
import com.cheering.user.dto.SignUpRequest;
import com.cheering.user.dto.SignUpResponse;
import com.cheering.user.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    public ResponseEntity<ResponseBodyDto<?>> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {

        userService.validateDuplicatedEmail(signUpRequest.email());
        userService.validateConfirmPassword(signUpRequest.password(), signUpRequest.passwordConfirm());

        User joinUser = userService.signup(signUpRequest);

        //jwt 토큰 생성
        JWToken jwToken = jwtGenerator.generateToken(
                String.valueOf(joinUser.getId()),
                List.of(new SimpleGrantedAuthority(Role.ROLE_USER.name())));

        //refresh token 저장 로직 구현 필요

        SignUpResponse signUpResponse = new SignUpResponse(joinUser.getId());

        return ResponseGenerator.signUpSuccess(jwToken, SIGN_UP_SUCCESS.getMessage(), signUpResponse);
    }

    @PostMapping("/signin")
    public ResponseEntity<ResponseBodyDto<?>> signIn() {

        return ResponseGenerator.success(SIGN_IN_SUCCESS.getMessage(), null);
    }

    @PostMapping("/email")
    public ResponseEntity<ResponseBodyDto<?>> validateEmail(@RequestBody Map<String, String> data) {
        String email = data.get("email");

        userService.validateEmailFormat(email);
        userService.validateDuplicatedEmail(email);

        return ResponseGenerator.success(VALIDATE_EMAIL_SUCCESS.getMessage(), null);
    }
}
