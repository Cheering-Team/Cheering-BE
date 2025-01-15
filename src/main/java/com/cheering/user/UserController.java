package com.cheering.user;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "전송 완료", userService.sendSMS(requestDTO)));
    }

    @PostMapping("/phone/code")
    public ResponseEntity<?> checkCode(@RequestBody UserRequest.CheckCodeDTO requestDTO) {
        userService.checkCode(requestDTO);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK,"인증 완료", null ));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody UserRequest.SignUpDTO requestDTO) {
        return ResponseEntity.ok()
                .body(ApiUtils.success(HttpStatus.CREATED, "회원가입 완료", userService.signUp(requestDTO)));
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(ApiUtils.success(HttpStatus.CREATED, "토큰 재발급 완료", userService.refresh(token)));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok()
                .body(ApiUtils.success(HttpStatus.OK, "유저 정보 조회", userService.getUserInfo(customUserDetails.getUser())));
    }

    @PutMapping("/users/name")
    public ResponseEntity<?> updateUserName(@RequestBody UserRequest.NameDTO requestDTO, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        userService.updateUserName(requestDTO, customUserDetails.getUser());
        return ResponseEntity.ok()
                .body(ApiUtils.success(HttpStatus.OK, "이름변경 완료", null));
    }

    @DeleteMapping("/users")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        userService.deleteUser(customUserDetails.getUser());
        return ResponseEntity.ok()
                .body(ApiUtils.success(HttpStatus.OK, "회원탈퇴 완료", null));
    }

    @PostMapping("/signin/kakao")
    public ResponseEntity<?> signInWithKakao(@RequestBody UserRequest.SocialTokenDTO requestDTO) {
        UserResponse.TokenDTO tokenDTO = userService.signInWithKakao(requestDTO.accessToken());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "로그인 완료", tokenDTO));
    }

    @PostMapping("/signin/naver")
    public ResponseEntity<?> signInWithNaver(@RequestBody UserRequest.SocialTokenDTO requestDTO) {
        UserResponse.TokenDTO tokenDTO = userService.signInWithNaver(requestDTO.accessToken());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "로그인 완료", tokenDTO));
    }

    @PostMapping("/signin/apple")
    public ResponseEntity<?> signInWithApple(@RequestBody UserRequest.SocialTokenDTO requestDTO) {
        UserResponse.TokenDTO tokenDTO = userService.signInWithApple(requestDTO);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "로그인 완료", tokenDTO));
    }

    @PostMapping("/phone/code/social")
    public ResponseEntity<?> checkCodeSocial(@RequestParam String type, @RequestBody UserRequest.SocialCheckCodeDTO requestDTO) {
        Object response = userService.checkCodeSocial(type, requestDTO);
        if(response instanceof UserResponse.UserWithCreatedAtDTO) {
            return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK,"존재하는 유저", response));
        } else {
            return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK,"회원가입 완료", response));
        }
    }

    @PostMapping("/connect")
    public ResponseEntity<?> socialConnect(@RequestParam String type, @RequestBody UserRequest.IdDTO requestDTO) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK,"계정 연결 완료", userService.socialConnect(type, requestDTO)));
    }

    @PostMapping("/fcm-token")
    public ResponseEntity<?> saveFCMToken(@RequestBody UserRequest.SaveFCMDTO requestDTO, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        userService.saveFCMToken(requestDTO, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK,"FCM 토큰 저장 완료", null));
    }

    @DeleteMapping("/fcm-token")
    public ResponseEntity<?> deleteFCMToken(@RequestParam String deviceId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if(customUserDetails == null){
            throw new CustomException(ExceptionCode.USER_NOT_FOUND);
        }
        userService.deleteFCMToken(deviceId, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK,"FCM 토큰 삭제 완료", null));
    }

    @GetMapping("/isFirstLogin")
    public ResponseEntity<?> isFirstLogin(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK,"조회 완료", userService.isFirstLogin(customUserDetails.getUser())));
    }

    // 유저의 나이와 성별 설정 여부 확인
    @GetMapping("/users/communities/{communityId}/check-profile")
    public ResponseEntity<?> getUserProfileStatus(@PathVariable("communityId") Long communityId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = customUserDetails.getUser();
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "프로필 설정 여부 확인 완료", userService.getUserProfileStatus(user.getId(), communityId)));
    }

    // 유저 나이, 성별 설정
    @PostMapping("/users/communities/{communityId}/set-profile")
    public ResponseEntity<?> setAgeAndGenderAndMeetProfile(@PathVariable("communityId") Long communityId, @AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody UserRequest.AgeAndGenderAndProfileDTO requestDTO) {
        User user = customUserDetails.getUser();
        userService.setAgeAndGenderAndMeetProfile(requestDTO, user.getId(), communityId);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "나이 및 성별, 모임 프로필 설정 완료", null));
    }

    // 유저의 나이, 성별 조회
    @GetMapping("/users/age-gender")
    public ResponseEntity<?> getAgeAndGender(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = customUserDetails.getUser();
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "나이 및 성별 조회 완료", userService.getAgeAndGender(user.getId())));
    }

    @PutMapping("/users/communities/{communityId}/update-profile")
    public ResponseEntity<?> updateMeetProfile(@PathVariable("communityId") Long communityId, @AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody UserRequest.UpdateMeetProfileDTO requestDTO) {
        User user = customUserDetails.getUser();
        userService.updateMeetProfile(requestDTO, user.getId(), communityId);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "모임 프로필 수정 완료", null));
    }
}

