package com.cheering.user.controller;

import static com.cheering.global.constant.SuccessMessage.SIGN_IN_SUCCESS;
import static com.cheering.global.constant.SuccessMessage.SIGN_UP_SUCCESS;
import static com.cheering.global.constant.SuccessMessage.VALIDATE_EMAIL_SUCCESS;

import com.cheering.auth.jwt.JWToken;
import com.cheering.auth.jwt.JwtGenerator;
import com.cheering.auth.redis.RedisRepository;
import com.cheering.global.constant.SuccessMessage;
import com.cheering.global.dto.ResponseBodyDto;
import com.cheering.global.dto.ResponseGenerator;
import com.cheering.global.exception.constant.ExceptionMessage;
import com.cheering.user.domain.Member;
import com.cheering.user.domain.Role;
import com.cheering.user.dto.SignInRequest;
import com.cheering.user.dto.SignInResponse;
import com.cheering.user.dto.SignUpRequest;
import com.cheering.user.dto.SignUpResponse;
import com.cheering.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    private final RedisRepository redisRepository;

    @PostMapping("/signup")
    public ResponseEntity<ResponseBodyDto<?>> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {

        userService.validateDuplicatedEmail(signUpRequest.email());
        userService.validateConfirmPassword(signUpRequest.password(), signUpRequest.passwordConfirm());

        Member joinUser = userService.signUp(signUpRequest);

        //jwt 토큰 생성
        JWToken jwToken = jwtGenerator.generateToken(
                String.valueOf(joinUser.getId()),
                List.of(new SimpleGrantedAuthority(Role.ROLE_USER.name())));

        SignUpResponse signUpResponse = new SignUpResponse(joinUser.getId());

        return ResponseGenerator.signSuccess(jwToken, SIGN_UP_SUCCESS, signUpResponse);
    }

    @PostMapping("/signin")
    public ResponseEntity<ResponseBodyDto<?>> signIn(@RequestBody SignInRequest signInRequest) {

        Member loginUser = userService.signIn(signInRequest);
        SignInResponse signInResponse = new SignInResponse(loginUser.getId());
        JWToken jwToken = jwtGenerator.generateToken(
                String.valueOf(loginUser.getId()),
                List.of(new SimpleGrantedAuthority(Role.ROLE_USER.name())));

        return ResponseGenerator.signSuccess(jwToken, SIGN_IN_SUCCESS, signInResponse);
    }

    @PostMapping("/email")
    public ResponseEntity<ResponseBodyDto<?>> validateEmail(@RequestBody Map<String, String> data) {
        String email = data.get("email");

        userService.validateEmailFormat(email);
        userService.validateDuplicatedEmail(email);

        return ResponseGenerator.success(VALIDATE_EMAIL_SUCCESS, null);
    }

    @GetMapping("/signout")
    public ResponseEntity<ResponseBodyDto<?>> signOut(HttpServletRequest request) {
        String refreshToken = (String) request.getHeader("Refresh-Token");

        String deleteValue = redisRepository.delete(refreshToken.substring(7));

        if (deleteValue == null) {
            return ResponseGenerator.fail(ExceptionMessage.FAIL_SIGN_OUT, null);
        }
        return ResponseGenerator.success(SuccessMessage.SIGN_OUT_SUCCESS, null);
    }
}
