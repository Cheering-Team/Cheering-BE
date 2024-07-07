package com.cheering.comment;

import com.cheering._core.errors.SuccessMessage;
import com.cheering._core.errors.ResponseBodyDto;
import com.cheering._core.errors.ResponseGenerator;
import java.util.List;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> writeComment(@PathVariable("postId") Long postId, @RequestBody CommentRequest.WriteCommentDTO requestDTO, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.CREATED, "댓글이 작성되었습니다.", commentService.writeComment(postId, requestDTO, customUserDetails.getUser())));
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable("postId") Long postId){
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "댓글들을 불러왔습니다.", commentService.getComments(postId)));
    }

//    @GetMapping("communities/{communityId}/posts/{postId}/comments")
//    public ResponseEntity<ResponseBodyDto<?>> getComments(@PathVariable("communityId") Long communityId,
//                                                          @PathVariable("postId") Long postId) {
//
//        List<CommentResponse> findComments = commentService.getComments(communityId, postId);
//        return ResponseGenerator.success(SuccessMessage.GET_COMMENT_SUCCESS, findComments);
//    }
//
//    @PostMapping("communities/{communityId}/posts/{postId}/comments")
//    public ResponseEntity<ResponseBodyDto<?>> createComment(@PathVariable("communityId") Long communityId,
//                                                            @PathVariable("postId") Long postId,
//                                                            @RequestBody CommentRequest commentRequest,
//                                                            @AuthenticationPrincipal Authentication user
//    ) {
//        System.out.println("loginUser" + user);
//        Long newCommentId = commentService.createComment(communityId, postId, commentRequest.content());
//
//        return ResponseGenerator.success(SuccessMessage.CREATE_COMMENT_SUCCESS, newCommentId);
//    }
}
