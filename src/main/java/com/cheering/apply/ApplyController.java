package com.cheering.apply;

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
public class ApplyController {
    private final ApplyService applyService;

    // 종목, 리그, 팀, 선수, 커뮤니티 등록 신청
    @PostMapping("/apply/community")
    public ResponseEntity<?> applyCommunity(@RequestBody ApplyRequest.ApplyCommunityDTO requestDTO, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        applyService.applyCommunity(requestDTO, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.CREATED, "신청 완료", null));
    }

    // 커뮤니티 신청 내역 조회
    @GetMapping("/apply/community")
    public ResponseEntity<?> getCommunityApplies(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.CREATED, "신청 완료", applyService.getCommunityApplies(customUserDetails.getUser())));
    }

    // 신청 삭제
    @DeleteMapping("/apply/{applyId}")
    public ResponseEntity<?> deleteApply(@PathVariable("applyId") Long applyId) {
        applyService.deleteApply(applyId);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.CREATED, "삭제 완료", null));
    }
}
