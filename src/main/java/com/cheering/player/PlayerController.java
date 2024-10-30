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

    // (선수 등록)
    @PostMapping("/teams/{teamId}/players")
    public ResponseEntity<?> registerPlayer(@PathVariable("teamId") Long teamId, @RequestBody PlayerRequest.RegisterCommunityDTO requestDTO){
        playerService.registerPlayer(teamId, requestDTO);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "커뮤니티 등록 완료", null));
    }
}
