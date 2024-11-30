package com.cheering.vote;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class VoteController {
    private final VoteService voteService;

    @GetMapping("/posts/{postId}/votes")
    public ResponseEntity<?> getVote(@PathVariable("postId") Long postId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "투표 조회 완료", voteService.getVote(postId, customUserDetails.getUser())));
    }

    @PostMapping("/votes/{voteOptionId}")
    public ResponseEntity<?> vote(@PathVariable("voteOptionId") Long voteOptionId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        voteService.vote(voteOptionId, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "투표 완료", null));
    }
}
