package com.cheering.comment;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> writeComment(@PathVariable("postId") Long postId, @RequestBody CommentRequest.WriteCommentDTO requestDTO, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.CREATED, "작성 완료", commentService.writeComment(postId, requestDTO, customUserDetails.getUser())));
    }

    // 댓글 목록 조회 (무한 스크롤)
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable("postId") Long postId,  @RequestParam int page, @RequestParam int size, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "댓글 목록 조회 완료", commentService.getComments(postId, pageable, customUserDetails.getUser())));
    }

    // 댓글 삭제하기
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long commentId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        commentService.deleteComment(commentId, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "댓글 삭제 완료", null));
    }

    // 데일리 랜덤 댓글 불러오기
    @GetMapping("/posts/{postId}/random-comments")
    public ResponseEntity<?> getRandomComment(@PathVariable("postId") Long postId, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "랜덤 댓글 조회 완료", commentService.getRandomComment(postId, customUserDetails.getUser())));
    }
}
