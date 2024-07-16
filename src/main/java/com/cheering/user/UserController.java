package com.cheering.user;

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
                .body(ApiUtils.success(HttpStatus.CREATED, "회원가입에 성공하였습니다.", userService.signUp(requestDTO)));
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
}
