package com.cheering.match;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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
    public ResponseEntity<?> getMatch(@PathVariable("matchId") Long matchId, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        User user = customUserDetails.getUser();
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "일정 조회 완료", matchService.getMatch(matchId, user)));
    }

    // 다음 경기 조회
    @GetMapping("/communities/{communityId}/matches/next")
    public ResponseEntity<?> getNextMatch(@PathVariable("communityId") Long communityId, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        User user = customUserDetails.getUser();
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "다음 경기 조회 완료", matchService.getNextMatch(communityId, user)));
    }

    // 일주일 전/후 경기 조회
    @GetMapping("/communities/{communityId}/matches/near")
    public ResponseEntity<?> getNearMatches(@PathVariable("communityId") Long communityId, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        User user = customUserDetails.getUser();
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "최근 경기 조회 완료", matchService.getNearMatches(communityId, user)));
    }

    // 특정 경기투표 포함 게시글 조회
    @GetMapping("/matches/{matchId}/communities/{communityId}/votes")
    public ResponseEntity<?> getVotes(@PathVariable("matchId") Long matchId, @PathVariable("communityId") Long communityId, @RequestParam String orderBy, @RequestParam int page, @RequestParam int size, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "투표 목록 조회 완료", matchService.getVotes(matchId, communityId, orderBy, pageable, customUserDetails.getUser())));
    }

    // 안끝난 경기 조회
    @GetMapping("/matches/unfinished")
    public ResponseEntity<?> getUnfinishedMatches(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "경기 목록 조회", matchService.getUnfinishedMatches(pageable)));

    }

    // 경기 수정
    @PutMapping("/matches/{matchId}")
    public ResponseEntity<?> editMatch(@PathVariable("matchId") Long matchId, @RequestBody MatchRequest.EditMatchDTO requestDTO) {
        matchService.editMatch(matchId, requestDTO);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "경기 수정", null));
    }


    // (일정 등록)
    @PostMapping("/matches")
    public ResponseEntity<?> addMatches(@RequestBody String jsonString){
        matchService.addMatches(jsonString);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "일정 등록 완료", null));
    }

    @GetMapping("/communities/{communityId}/matches/twoweeks")
    public ResponseEntity<?> getTwoWeeksMatches(@PathVariable("communityId") Long communityId
    ,CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "커뮤니티의 2주 내 경기 조회 완료", matchService.getTwoWeeksMatches(communityId, user)));
    }
}
