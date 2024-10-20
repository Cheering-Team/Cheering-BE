package com.cheering.comment.reComment;

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
public class ReCommentController {

    private final ReCommentService reCommentService;

    // 답글 남기기
    @PostMapping("/comments/{commentId}/re")
    public ResponseEntity<?> writeReComment(@PathVariable("commentId") Long commentId, @RequestBody ReCommentRequest.WriteReCommentDTO requestDTO, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.CREATED, "답글 작성 완료", reCommentService.writeReComment(commentId, requestDTO, customUserDetails.getUser())));
    }

    // 답글 불러오기
    @GetMapping("/comments/{commentId}/re")
    public ResponseEntity<?> getReComments(@PathVariable("commentId") Long commentId, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "답글 조회 완료", reCommentService.getReComments(commentId, customUserDetails.getUser())));
    }

    @DeleteMapping("/reComments/{reCommentId}")
    public ResponseEntity<?> deleteReComment(@PathVariable("reCommentId") Long reCommentId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        reCommentService.deleteReComment(reCommentId, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "답글 삭제 완료", null));
    }
}
