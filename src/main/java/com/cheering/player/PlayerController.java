package com.cheering.player;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import com.cheering.team.TeamRequest;
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

    // 선수 검색
    @GetMapping("/players")
    public ResponseEntity<?> getPlayers(@RequestParam("name") String name, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "선수들을 검색했습니다.", playerService.getPlayers(name, userDetails.getUser())));
    }

    // 특정 팀 선수목록 불러오기
    @GetMapping("/teams/{teamId}/players")
    public ResponseEntity<?> getPlayersByTeam(@PathVariable("teamId") Long teamId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "선수들을 불러왔습니다.", playerService.getPlayersByTeam(teamId, userDetails.getUser())));
    }

    // 특정 선수 정보 불러오기
    @GetMapping("/players/{playerId}")
    public ResponseEntity<?> getPlayerInfo(@PathVariable("playerId") Long playerId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "선수의 정보를 불러왔습니다.", playerService.getPlayerInfo(playerId, userDetails.getUser())));
    }

    // 커뮤니티 가입
    @PostMapping("/players/{playerId}/users")
    public ResponseEntity<?> joinCommunity(@PathVariable Long playerId, @RequestPart("nickname") String nickname, @RequestPart(value = "image", required = false) MultipartFile image, @AuthenticationPrincipal CustomUserDetails userDetails) {
        playerService.joinCommunity(playerId, nickname, image, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "가입이 완료되었습니다.", null));
    }

    // 내가 가입한 선수 목록 불러오기
    @GetMapping("/my/players")
    public ResponseEntity<?> getMyPlayers(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "내 선수 목록을 불러왔습니다.", playerService.getMyPlayers(userDetails.getUser())));
    }

    // 선수 등록
    @PostMapping("/teams/{teamId}/players")
    public ResponseEntity<?> registerPlayer(@PathVariable("teamId") Long teamId, @RequestBody PlayerRequest.RegisterPlayerDTO requestDTO){
        playerService.registerPlayer(teamId, requestDTO);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "선수를 등록하였습니다.", null));
    }
}
