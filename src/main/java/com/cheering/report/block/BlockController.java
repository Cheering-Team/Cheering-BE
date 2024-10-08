package com.cheering.report.block;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BlockController {
    private final BlockService blockService;

    // 유저 차단
    @PostMapping("/blocks/{playerUserId}")
    ResponseEntity<?> blockUser(@PathVariable("playerUserId") Long playerUserId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        blockService.blockUser(playerUserId, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.CREATED, "해당 유저를 차단했습니다.", null));
    }

    // 차단한 계정 목록 불러오기
    @GetMapping("/blocks/{playerUserId}")
    ResponseEntity<?> getBlockedUsers(@PathVariable("playerUserId") Long playerUserId) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "차단한 계정들을 불러왔습니다.", blockService.getBlockedUsers(playerUserId)));
    }

    // 차단 해제
    @DeleteMapping("/blocks/{playerUserId}")
    ResponseEntity<?> unblockUser(@PathVariable("playerUserId") Long playerUserId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        blockService.unblockUser(playerUserId, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.CREATED, "차단을 해제했습니다.", null));
    }
}
