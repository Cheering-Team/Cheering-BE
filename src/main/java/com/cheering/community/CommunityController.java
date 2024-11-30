package com.cheering.community;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommunityController {
    private final CommunityService communityService;

    // 커뮤니티 조회
    @GetMapping("/communities/{communityId}")
    public ResponseEntity<?> getCommunityById(@PathVariable("communityId") Long communityId, @AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "커뮤니티 조회 완료", communityService.getCommunityById(communityId, userDetails.getUser())));
    }

    // 커뮤니티 가입
    @PostMapping("/communities/{communityId}/fans")
    public ResponseEntity<?> joinCommunity(@PathVariable Long communityId, @RequestPart(value = "name", required = false) String name, @AuthenticationPrincipal CustomUserDetails userDetails) {
        communityService.joinCommunity(communityId, name, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "커뮤니티 가입 완료", null));
    }

    // 내가 가입한 커뮤니티 조회
    @GetMapping("/my/communities")
    public ResponseEntity<?> getMyCommunities(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "내 커뮤니티 조회 완료", communityService.getMyCommunities(userDetails.getUser())));
    }

    // 커뮤니티 순서 변경
    @PutMapping("/communities/order")
    public ResponseEntity<?> changeCommunityOrder(@RequestBody List<CommunityRequest.ChangeOrderRequest> changeOrderRequests, @AuthenticationPrincipal CustomUserDetails userDetails) {
        communityService.changeCommunityOrder(changeOrderRequests, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "커뮤니티 순서 변겨 완료", null));
    }

    // 커뮤니티 모두 가입 (신규 유저)
    @PostMapping("/fans")
    public ResponseEntity<?> joinCommunities(@RequestBody CommunityRequest.JoinCommunitiesRequest requestDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {
        communityService.joinCommunities(requestDTO, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "커뮤니티 가입 완료", null));
    }

    // 랜덤 커뮤니티 조회
    @GetMapping("/communities/random")
    public ResponseEntity<?> getRandomCommunity() {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "커뮤니티 검색 완료", communityService.getRandomCommunity()));
    }
}
