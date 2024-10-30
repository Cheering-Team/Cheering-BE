package com.cheering.match;

import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MatchController {
    private final MatchService matchService;

    // 일정 목록 조회
    @GetMapping("/communities/{communityId}/matches")
    public ResponseEntity<?> getMatchSchedule(@PathVariable("communityId") Long communityId){
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "일정 조회 목록 완료", matchService.getMatchSchedule(communityId)));
    }

    // 일정 조회
    @GetMapping("/matches/{matchId}")
    public ResponseEntity<?> getMatch(@PathVariable("matchId") Long matchId){
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "일정 조회 완료", matchService.getMatch(matchId)));
    }

    // (일정 등록)
    @PostMapping("/leagues/{leagueId}/matches")
    public ResponseEntity<?> addMatches(@PathVariable("leagueId") Long leagueId, @RequestBody MatchRequest.MatchListDTO requestDTO){
        matchService.addMatches(leagueId, requestDTO.matches());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "일정 등록 완료", null));
    }
}
