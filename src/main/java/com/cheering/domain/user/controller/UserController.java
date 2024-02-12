package com.cheering.domain.user.controller;

import static com.cheering.global.constant.SuccessMessage.SIGN_IN_SUCCESS;
import static com.cheering.global.constant.SuccessMessage.SIGN_UP_SUCCESS;
import static com.cheering.global.constant.SuccessMessage.VALIDATE_EMAIL_SUCCESS;

import com.cheering.auth.jwt.JWToken;
import com.cheering.auth.jwt.JwtConstant;
import com.cheering.auth.jwt.JwtGenerator;
import com.cheering.auth.jwt.JwtProvider;
import com.cheering.auth.redis.RedisRepository;
import com.cheering.domain.community.dto.response.SearchCommunityResponse;
import com.cheering.domain.user.domain.User;
import com.cheering.domain.user.dto.request.SignInRequest;
import com.cheering.domain.user.dto.request.SignUpRequest;
import com.cheering.domain.user.dto.response.SignInResponse;
import com.cheering.domain.user.dto.response.SignUpResponse;
import com.cheering.domain.user.service.UserService;
import com.cheering.global.constant.SuccessMessage;
import com.cheering.global.dto.ResponseBodyDto;
import com.cheering.global.dto.ResponseGenerator;
import com.cheering.global.exception.constant.ExceptionMessage;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtGenerator jwtGenerator;
    private final JwtProvider jwtTokenProvider;
    private final RedisRepository redisRepository;

    @PostMapping("/signup")
    public ResponseEntity<ResponseBodyDto<?>> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {

        userService.validateDuplicatedEmail(signUpRequest.email());
        userService.validateConfirmPassword(signUpRequest.password(), signUpRequest.passwordConfirm());

        User joinUser = userService.signUp(signUpRequest);

        //jwt 토큰 생성
        JWToken jwToken = jwtGenerator.generateToken(
                String.valueOf(joinUser.getId()),
                List.of(new SimpleGrantedAuthority(joinUser.getRole().toString())));

        SignUpResponse signUpResponse = new SignUpResponse(joinUser.getId());

        return ResponseGenerator.signSuccess(jwToken, SIGN_UP_SUCCESS, signUpResponse);
    }

    @PostMapping("/signin")
    public ResponseEntity<ResponseBodyDto<?>> signIn(@RequestBody SignInRequest signInRequest) {

        User loginUser = userService.signIn(signInRequest);
        SignInResponse signInResponse = new SignInResponse(loginUser.getId());
        JWToken jwToken = jwtGenerator.generateToken(
                String.valueOf(loginUser.getId()),
                List.of(new SimpleGrantedAuthority(loginUser.getRole().toString())));

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

    @GetMapping("/users/communities")
    public ResponseEntity<ResponseBodyDto<?>> getUserCommunities() {
        List<SearchCommunityResponse> userCommunities = userService.getUserCommunities();
        return ResponseGenerator.success(SuccessMessage.SEARCH_COMMUNITY_SUCCESS, userCommunities);
    }

    @GetMapping("refresh")
    public ResponseEntity<ResponseBodyDto<?>> reIssueAccessToken(HttpServletRequest request,
                                                                 HttpServletResponse response) {
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
        String newAccessToken;

        try {
            if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken, request)) {
                log.info("Authenticated User");
                newAccessToken = jwtTokenProvider.reIssueAccessToken(refreshToken);

                response.setHeader("Access-Token", JwtConstant.GRANT_TYPE + " " + newAccessToken);
                return ResponseGenerator.success(SuccessMessage.REISSUE_ACCESS_TOKEN_SUCCESS, null);
            }
        } catch (ExpiredJwtException e) {
            log.error("expired Refresh-Token", e);

            return ResponseGenerator.fail(ExceptionMessage.EXPIRED_REFRESH_TOKEN, null);
        }

        return null;
    }
}
