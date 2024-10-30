package com.cheering.community;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import com.cheering.player.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    // 특정 팀 소속 커뮤니티 목록 조회
    @GetMapping("/teams/{teamId}/communities")
    public ResponseEntity<?> getCommunitiesByTeam(@PathVariable("teamId") Long teamId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "선수 목록 조회 완료", communityService.getCommunitiesByTeam(teamId, userDetails.getUser())));
    }

    // 커뮤니티 검색
    @GetMapping("/communities")
    public ResponseEntity<?> getCommunities(@RequestParam("name") String name, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "커뮤니티 검색 완료", communityService.getCommunities(name, userDetails.getUser())));
    }

    // 커뮤니티 가입
    @PostMapping("/communities/{communityId}/fans")
    public ResponseEntity<?> joinCommunity(@PathVariable Long communityId, @RequestPart(value = "name", required = false) String name, @RequestPart(value = "image", required = false) MultipartFile image, @AuthenticationPrincipal CustomUserDetails userDetails) {
        communityService.joinCommunity(communityId, name, image, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "커뮤니티 가입 완료", null));
    }

    // 내가 가입한 커뮤니티 조회
    @GetMapping("/my/communities")
    public ResponseEntity<?> getMyCommunities(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "내 커뮤니티 조회 완료", communityService.getMyCommunities(userDetails.getUser())));
    }
}
