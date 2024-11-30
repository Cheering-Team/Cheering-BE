package com.cheering.match;

import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MatchController {
    private final MatchService matchService;

    // 일정 목록 조회
    @GetMapping("/communities/{communityId}/matches")
    public ResponseEntity<?> getMatchSchedule(@PathVariable("communityId") Long communityId, @RequestParam("year") int year,
    @RequestParam("month") int month){
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "일정 조회 목록 완료", matchService.getMatchSchedule(communityId, year, month)));
    }

    // 일정 조회
    @GetMapping("/matches/{matchId}")
    public ResponseEntity<?> getMatch(@PathVariable("matchId") Long matchId){
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "일정 조회 완료", matchService.getMatch(matchId)));
    }

    // 다음 경기 조회
    @GetMapping("/communities/{communityId}/matches/next")
    public ResponseEntity<?> getNextMatch(@PathVariable("communityId") Long communityId){
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "다음 경기 조회 완료", matchService.getNextMatch(communityId)));
    }

    // 일주일 전/후 경기 조회
    @GetMapping("/communities/{communityId}/matches/near")
    public ResponseEntity<?> getNearMatches(@PathVariable("communityId") Long communityId){
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "최근 경기 조회 완료", matchService.getNearMatches(communityId)));
    }

    // (일정 등록)
    @PostMapping("/matches")
    public ResponseEntity<?> addMatches(@RequestBody String jsonString){
        matchService.addMatches(jsonString);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "일정 등록 완료", null));
    }
}
