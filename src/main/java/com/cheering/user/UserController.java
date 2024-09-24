package com.cheering.user;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    // 인증번호 전송
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
    public ResponseEntity<?> signUp(@RequestBody UserRequest.SignUpDTO requestDTO) {
        return ResponseEntity.ok()
                .body(ApiUtils.success(HttpStatus.CREATED, "회원가입이 완료되었습니다.", userService.signUp(requestDTO)));
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(ApiUtils.success(HttpStatus.CREATED, "토큰이 재발급 되었습니다.", userService.refresh(token)));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok()
                .body(ApiUtils.success(HttpStatus.OK, "유저 정보를 불러왔습니다.", userService.getUserInfo(customUserDetails.getUser())));
    }

    @PutMapping("/users/nickname")
    public ResponseEntity<?> updateUserNickname(@RequestBody UserRequest.NicknameDTO requestDTO, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        userService.updateUserNickname(requestDTO, customUserDetails.getUser());
        return ResponseEntity.ok()
                .body(ApiUtils.success(HttpStatus.OK, "닉네임을 변경하였습니다.", null));
    }

    @DeleteMapping("/users")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        userService.deleteUser(customUserDetails.getUser());
        return ResponseEntity.ok()
                .body(ApiUtils.success(HttpStatus.OK, "회원탈퇴되었습니다.", null));
    }

    @PostMapping("/signin/kakao")
    public ResponseEntity<?> signInWithKakao(@RequestParam String accessToken) {
        UserResponse.TokenDTO tokenDTO = userService.signInWithKakao(accessToken);
        if(tokenDTO == null) {
            return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "카카오 회원가입이 필요합니다.", null));
        }
        else {
            return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "로그인되었습니다.", tokenDTO));
        }
    }

    @PostMapping("/signin/naver")
    public ResponseEntity<?> signInWithNaver(@RequestParam String accessToken) {
        Object response = userService.signInWithNaver(accessToken);
        if(response instanceof UserResponse.UserWithCreatedAtDTO) {
            return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK,"이미 가입된 유저입니다.", response));
        } else if(response instanceof UserResponse.SignUpTokenDTO){
            return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK,"회원가입되었습니다.", response));
        } else {
            return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK,"로그인되었습니다.", response));
        }
    }

    @PostMapping("/phone/code/kakao")
    public ResponseEntity<?> checkCodeKakao(@RequestParam String accessToken, @RequestBody UserRequest.CheckCodeDTO requestDTO) {
        Object response = userService.checkCodeKakao(accessToken, requestDTO);
        if(response instanceof UserResponse.UserWithCreatedAtDTO) {
            return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK,"이미 가입된 유저입니다.", response));
        } else {
            return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK,"회원가입되었습니다.", response));
        }
    }

    @PostMapping("/connect")
    public ResponseEntity<?> socialConnect(@RequestParam String accessToken, @RequestParam String type, @RequestBody UserRequest.IdDTO requestDTO) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK,"계정이 연결되었습니다.", userService.socialConnect(accessToken, type, requestDTO)));
    }

    @PostMapping("/fcm-token")
    public ResponseEntity<?> saveFCMToken(@RequestParam String token, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        userService.saveFCMToken(token, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK,"FCM 토큰이 저장되었습니다.", null));
    }

    @DeleteMapping("/fcm-token")
    public ResponseEntity<?> deleteFCMToken(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if(customUserDetails == null){
            throw new CustomException(ExceptionCode.USER_NOT_FOUND);
        }
        userService.deleteFCMToken(customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK,"FCM 토큰이 삭제되었습니다.", null));
    }
}
