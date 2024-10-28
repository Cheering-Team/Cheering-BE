package com.cheering.team;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import com.cheering._core.util.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TeamController {
    private final TeamService teamService;
    @GetMapping("/leagues/{leagueId}/teams")
    public ResponseEntity<?> getTeams(@PathVariable("leagueId") Long leagueId){
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "팀 목록 조회 완료", teamService.getTeams(leagueId)));
    }

    @GetMapping("/players/{playerId}/teams")
    public ResponseEntity<?> getTeamsByPlayer(@PathVariable("playerId") Long playerId){
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "팀 목록 조회 완료", teamService.getTeamsByPlayer(playerId)));
    }

//    // (팀 등록)
//    @PostMapping("/leagues/{leagueId}/teams")
//    public ResponseEntity<?> registerTeam(@PathVariable("leagueId") Long leagueId, @RequestBody TeamRequest.RegisterTeamDTO requestDTO){
//        teamService.registerTeam(leagueId, requestDTO);
//        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "팀 등록 완료", null));
//    }
}
