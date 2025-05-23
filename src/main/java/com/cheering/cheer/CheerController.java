package com.cheering.cheer;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import com.cheering.comment.CommentRequest;
import com.cheering.post.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CheerController {
    private final CheerService cheerService;

    // 응원 작성
    @PostMapping("/matches/{matchId}/communities/{communityId}/cheers")
    public ResponseEntity<?> writeCheer(@PathVariable("matchId") Long matchId, @PathVariable("communityId") Long communityId, @RequestBody
    CommentRequest.WriteCommentDTO requestDTO, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        cheerService.writeCheer(matchId, communityId, requestDTO, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.CREATED, "작성 완료", null));
    }

    // 응원 목록 조회
    @GetMapping("/matches/{matchId}/communities/{communityId}/cheers")
    public ResponseEntity<?> getCheers(@PathVariable("matchId") Long matchId, @PathVariable("communityId") Long communityId,  @RequestParam int page, @RequestParam int size, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "응원 목록 조회 완료", cheerService.getCheers(matchId, communityId, pageable, customUserDetails.getUser())));
    }

    // 응원 삭제
    @DeleteMapping("/cheers/{cheerId}")
    public ResponseEntity<?> deleteCheer(@PathVariable("cheerId") Long cheerId) {
        cheerService.deleteCheer(cheerId);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "응원 삭제 완료", null));
    }

    // 응원 좋아요
    @PostMapping("/communities/{communityId}/cheers/{cheerId}/likes")
    public ResponseEntity<?> likeCheer(@PathVariable("communityId") Long communityId, @PathVariable("cheerId") Long cheerId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        cheerService.likeCheer(communityId, cheerId, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "좋아요 완료", null));
    }

    // 응원 좋아요 취소
    @DeleteMapping("/communities/{communityId}/cheers/{cheerId}/likes")
    public ResponseEntity<?> deleteLikeCheer(@PathVariable("communityId") Long communityId, @PathVariable("cheerId") Long cheerId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        cheerService.deleteLikeCheer(communityId, cheerId, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "좋아요 취소", null));
    }
}
