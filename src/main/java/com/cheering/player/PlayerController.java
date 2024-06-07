package com.cheering.player;

import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PlayerController {
    private final PlayerService playerService;

    @GetMapping("/teams/{teamId}/players")
    public ResponseEntity<?> getPlayersByTeam(@PathVariable("teamId") Long teamId) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "선수들을 불러왔습니다.", playerService.getPlayersByTeam(teamId)));
    }

    @GetMapping("/players/{playerId}")
    public ResponseEntity<?> getPlayerInfo(@PathVariable("playerId") Long playerId) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "선수의 정보를 불러왔습니다.", playerService.getPlayerInfo(playerId)));
    }
}
