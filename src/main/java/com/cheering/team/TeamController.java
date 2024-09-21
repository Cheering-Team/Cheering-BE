package com.cheering.team;

import com.cheering._core.util.ApiUtils;
import com.cheering._core.util.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TeamController {
    private final TeamService teamService;
    private final S3Util s3Util;

    @GetMapping("/leagues/{leagueId}/teams")
    public ResponseEntity<?> getTeams(@PathVariable("leagueId") Long leagueId){
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "팀들을 불러왔습니다.", teamService.getTeams(leagueId)));
    }
}
