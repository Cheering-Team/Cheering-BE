package com.cheering.player;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PlayerController {
    private final PlayerService playerService;
    // 선수 검색 (무한 스크롤)
    @GetMapping("/players")
    public ResponseEntity<?> searchPlayers(
            @RequestParam(name = "teamId", required = false) Long teamId,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam int page, @RequestParam int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "선수 검색 완료", playerService.searchPlayers(name, teamId, pageable, userDetails.getUser())));
    }

    // 인기 선수 조회
    @GetMapping("/players/popular")
    public ResponseEntity<?> getPopularPlayers(){
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "인기 선수 조회 완료", playerService.getPopularPlayers()));
    }

    // (선수 등록)
    @PostMapping("/teams/{teamId}/players")
    public ResponseEntity<?> registerPlayer(@PathVariable("teamId") Long teamId, @RequestBody List<PlayerRequest.RegisterCommunityDTO> requestDTO){
        playerService.registerPlayer(teamId, requestDTO);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "커뮤니티 등록 완료", null));
    }
}
