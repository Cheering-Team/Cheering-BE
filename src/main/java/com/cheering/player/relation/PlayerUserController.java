package com.cheering.player.relation;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import com.cheering.user.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PlayerUserController {
    private final PlayerUserService playerUserService;

    // 프로필 정보 불러오기
    @GetMapping("/playerusers/{playerUserId}")
    public ResponseEntity<?> getPlayerUserInfo(@PathVariable("playerUserId") Long playerUserId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "프로필 정보를 불러왔습니다.", playerUserService.getPlayerUserInfo(playerUserId, userDetails.getUser())));
    }

    // 유저의 모든 글 불러오기
    @GetMapping("/playerusers/{playerUserId}/posts")
    public ResponseEntity<?> getPlayerUserPosts(@PathVariable("playerUserId") Long playerUserId, @RequestParam int page, @RequestParam int size, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "게시글 목록을 불러왔습니다.", playerUserService.getPlayerUserPosts(playerUserId, pageable, customUserDetails.getUser())));
    }

    // 커뮤니티 프로필 이미지 변경
    @PutMapping("/playerusers/{playerUserId}/image")
    public ResponseEntity<?> updatePlayerUserImage(@PathVariable("playerUserId") Long playerUserId, @RequestPart(value = "image", required = false) MultipartFile image) {
        playerUserService.updatePlayerUserImage(playerUserId, image);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "프로필 이미지를 수정했습니다.", null));
    }

    // 커뮤니티 닉네임 변경
    @PutMapping("/playerusers/{playerUserId}/nickname")
    public ResponseEntity<?> updatePlayerUserNickname(@PathVariable("playerUserId") Long playerUserId, @RequestBody UserRequest.NicknameDTO requestDTO) {
        playerUserService.updatePlayerUserNickname(playerUserId, requestDTO);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "닉네임을 변경하였습니다.", null));
    }
}
