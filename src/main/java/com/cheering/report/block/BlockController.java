package com.cheering.report.block;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BlockController {
    private final BlockService blockService;

    // 유저 차단
    @PostMapping("/block/{playerUserId}")
    ResponseEntity<?> blockUser(@PathVariable("playerUserId") Long playerUserId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        blockService.blockUser(playerUserId, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.CREATED, "해당 유저를 차단했습니다.", null));
    }
}
