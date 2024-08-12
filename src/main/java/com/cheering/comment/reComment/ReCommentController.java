package com.cheering.comment.reComment;

import com.cheering._core.errors.SuccessMessage;
import com.cheering._core.errors.ResponseBodyDto;
import com.cheering._core.errors.ResponseGenerator;
import java.util.List;

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

    @PostMapping("/comments/{commentId}/re")
    public ResponseEntity<?> writeReComment(@PathVariable("commentId") Long commentId, @RequestBody ReCommentRequest.WriteReCommentDTO requestDTO, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.CREATED, "답글이 작성되었습니다.", reCommentService.writeReComment(commentId, requestDTO, customUserDetails.getUser())));
    }

    @GetMapping("/comments/{commentId}/re")
    public ResponseEntity<?> getReComments(@PathVariable("commentId") Long commentId, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "답글들을 불러왔습니다.", reCommentService.getComments(commentId, customUserDetails.getUser())));
    }

    @DeleteMapping("/reComments/{reCommentId}")
    public ResponseEntity<?> deleteReComment(@PathVariable("reCommentId") Long reCommentId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        reCommentService.deleteReComment(reCommentId, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "답글이 삭제되었습니다.", null));
    }

//    @PostMapping("/communities/{communityId}/posts/{postId}/comments/{commentId}/recomments")
//    public ResponseEntity<ResponseBodyDto<?>> createReComment(@PathVariable("communityId") Long communityId,
//                                                              @PathVariable("postId") Long postId,
//                                                              @PathVariable("commentId") Long commentId,
//                                                              @RequestBody CommentRequest commentRequest) {
//
//        Long newReCommentId = reCommentService.createReComment(communityId, postId, commentId,
//                commentRequest.content());
//
//        return ResponseGenerator.success(SuccessMessage.CREATE_RE_COMMENT_SUCCESS, newReCommentId);
//    }

//    @GetMapping("/communities/{communityId}/posts/{postId}/comments/{commentId}/recomments")
//    public ResponseEntity<ResponseBodyDto<?>> getReComments(@PathVariable("communityId") Long communityId,
//                                                            @PathVariable("postId") Long postId,
//                                                            @PathVariable("commentId") Long commentId) {
//        List<ReCommentResponse> findReComments = reCommentService.getReComments(commentId);
//
//        return ResponseGenerator.success(SuccessMessage.GET_RE_COMMENT_SUCCESS, findReComments);
//    }
}
