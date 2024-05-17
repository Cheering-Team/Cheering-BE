package com.cheering.user;

import com.cheering._core.security.JwtGenerator;
import com.cheering._core.security.JwtProvider;
import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final JwtGenerator jwtGenerator;
    private final JwtProvider jwtTokenProvider;

    @PostMapping("/phone/sms")
    public ResponseEntity<?> sendSMS(@RequestBody UserRequest.SendSMSDTO requestDTO) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "인증번호가 전송되었습니다.", userService.sendSMS(requestDTO)));
    }

    @PostMapping("/phone/code")
    public ResponseEntity<?> checkCode(@RequestBody UserRequest.CheckCodeDTO requestDTO) {
        userService.checkCode(requestDTO);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK,"인증번호가 일치합니다.", null ));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp (@RequestBody UserRequest.SignUpDTO requestDTO) {
        UserResponse.TokenDTO responseDTO = userService.signUp(requestDTO);
        ResponseCookie responseCookie = setRefreshTokenCookie(responseDTO.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(ApiUtils.success(HttpStatus.OK, "회원가입에 성공하였습니다.", new UserResponse.AccessTokenDTO(responseDTO.accessToken())));
    }

    public ResponseCookie setRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(JwtProvider.REFRESH_EXP)
                .build();
    }

//    @PostMapping("/signup")
//    public ResponseEntity<ResponseBodyDto<?>> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
//
//        userService.validateDuplicatedEmail(signUpRequest.email());
//        userService.validateConfirmPassword(signUpRequest.password(), signUpRequest.passwordConfirm());
//
//        User joinUser = userService.signUp(signUpRequest);
//
//        //jwt 토큰 생성
//        JWToken jwToken = jwtGenerator.generateToken(
//                String.valueOf(joinUser.getId()),
//                List.of(new SimpleGrantedAuthority(joinUser.getRole().toString())));
//
//        SignUpResponse signUpResponse = new SignUpResponse(joinUser.getId());
//
//        return ResponseGenerator.signSuccess(jwToken, SIGN_UP_SUCCESS, signUpResponse);
//    }
//
//    @PostMapping("/signin")
//    public ResponseEntity<ResponseBodyDto<?>> signIn(@RequestBody UserRequest userRequest) {
//
//        User loginUser = userService.signIn(userRequest);
//        SignInResponse signInResponse = new SignInResponse(loginUser.getId());
//        JWToken jwToken = jwtGenerator.generateToken(
//                String.valueOf(loginUser.getId()),
//                List.of(new SimpleGrantedAuthority(loginUser.getRole().toString())));
//
//        return ResponseGenerator.signSuccess(jwToken, SIGN_IN_SUCCESS, signInResponse);
//    }
//
//    @PostMapping("/email")
//    public ResponseEntity<ResponseBodyDto<?>> validateEmail(@RequestBody Map<String, String> data) {
//        String email = data.get("email");
//
//        userService.validateEmailFormat(email);
//        userService.validateDuplicatedEmail(email);
//
//        return ResponseGenerator.success(VALIDATE_EMAIL_SUCCESS, null);
//    }
//
//    @GetMapping("/signout")
//    public ResponseEntity<ResponseBodyDto<?>> signOut(HttpServletRequest request) {
//        String refreshToken = request.getHeader(JwtConstant.REFRESH_TOKEN_KEY_NAME);
//
//        String deleteValue = redisRepository.delete(refreshToken.substring(7));
//
//        if (deleteValue == null) {
//            return ResponseGenerator.fail(ExceptionMessage.FAIL_SIGN_OUT, null);
//        }
//
//        return ResponseGenerator.success(SuccessMessage.SIGN_OUT_SUCCESS, null);
//    }
//
//    @GetMapping("/users/communities")
//    public ResponseEntity<ResponseBodyDto<?>> getUserCommunities() {
//        List<SearchCommunityResponse> userCommunities = userService.getUserCommunities();
//        return ResponseGenerator.success(SuccessMessage.SEARCH_COMMUNITY_SUCCESS, userCommunities);
//    }
//
//    @GetMapping("/refresh")
//    public ResponseEntity<ResponseBodyDto<?>> reIssueAccessToken(HttpServletRequest request) {
//        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
//        String newAccessToken = jwtTokenProvider.getAccessToken(refreshToken);
//
//        return ResponseGenerator.success(SuccessMessage.REISSUE_ACCESS_TOKEN_SUCCESS, newAccessToken);
//    }
}
