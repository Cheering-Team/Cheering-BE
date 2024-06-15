package com.cheering.player;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PlayerController {
    private final PlayerService playerService;

    @GetMapping("/teams/{teamId}/players")
    public ResponseEntity<?> getPlayersByTeam(@PathVariable("teamId") Long teamId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "선수들을 불러왔습니다.", playerService.getPlayersByTeam(teamId, userDetails.getUser())));
    }

    @GetMapping("/players/{playerId}")
    public ResponseEntity<?> getPlayerInfo(@PathVariable("playerId") Long playerId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "선수의 정보를 불러왔습니다.", playerService.getPlayerInfo(playerId, userDetails.getUser())));
    }

    @GetMapping("/players/{playerId}/nickname")
    public ResponseEntity<?> checkNickname(@PathVariable("playerId") Long playerId, @RequestParam("nickname") String nickname) {
        playerService.checkNickname(playerId, nickname);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "사용 가능한 닉네임 입니다.", null));
    }

    @PostMapping("/players/{playerId}/users")
    public ResponseEntity<?> joinCommunity(@PathVariable Long playerId, @RequestPart("nickname") String nickname, @RequestPart("image") MultipartFile image, @AuthenticationPrincipal CustomUserDetails userDetails) {
        playerService.joinCommunity(playerId, nickname, image, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "가입이 완료되었습니다.", null));
    }
}
