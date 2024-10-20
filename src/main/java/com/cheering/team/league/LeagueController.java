package com.cheering.team.league;

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
public class LeagueController {
    private final LeagueService leagueService;

    @GetMapping("/sports/{sportId}/leagues")
    public ResponseEntity<?> getLeagues(@PathVariable("sportId") Long sportId){
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "리그 조회 완료", leagueService.getLeagues(sportId)));
    }
}
