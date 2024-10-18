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

    // 팬 차단
    @PostMapping("/blocks/{fanId}")
    ResponseEntity<?> blockFan(@PathVariable("fanId") Long fanId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        blockService.blockFan(fanId, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.CREATED, "차단 완료", null));
    }

    // 차단한 팬 목록 조회
    @GetMapping("/blocks/{fanId}")
    ResponseEntity<?> getBlockedFans(@PathVariable("fanId") Long fanId) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "차단 목록 조회 완료", blockService.getBlockedFans(fanId)));
    }

    // 차단 해제
    @DeleteMapping("/blocks/{fanId}")
    ResponseEntity<?> unblockFan(@PathVariable("fanId") Long fanId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        blockService.unblockFan(fanId, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.CREATED, "차단 해제 완료", null));
    }
}
