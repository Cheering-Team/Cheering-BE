package com.cheering.user;

import static com.cheering._core.errors.SuccessMessage.SIGN_IN_SUCCESS;
import static com.cheering._core.errors.SuccessMessage.SIGN_UP_SUCCESS;
import static com.cheering._core.errors.SuccessMessage.VALIDATE_EMAIL_SUCCESS;

import com.cheering._core.security.JWToken;
import com.cheering._core.security.JwtConstant;
import com.cheering._core.security.JwtGenerator;
import com.cheering._core.security.JwtProvider;
import com.cheering._core.redis.RedisRepository;
import com.cheering.community.SearchCommunityResponse;
import com.cheering._core.errors.SuccessMessage;
import com.cheering._core.errors.ResponseBodyDto;
import com.cheering._core.errors.ResponseGenerator;
import com.cheering._core.errors.ExceptionMessage;
import jakarta.servlet.http.HttpServletRequest;
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
        String refreshToken = request.getHeader(JwtConstant.REFRESH_TOKEN_KEY_NAME);

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

    @GetMapping("/refresh")
    public ResponseEntity<ResponseBodyDto<?>> reIssueAccessToken(HttpServletRequest request) {
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
        String newAccessToken = jwtTokenProvider.getAccessToken(refreshToken);

        return ResponseGenerator.success(SuccessMessage.REISSUE_ACCESS_TOKEN_SUCCESS, newAccessToken);
    }
}
