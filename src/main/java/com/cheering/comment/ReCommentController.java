package com.cheering.comment;

import com.cheering._core.errors.SuccessMessage;
import com.cheering._core.errors.ResponseBodyDto;
import com.cheering._core.errors.ResponseGenerator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReCommentController {

    private final ReCommentService reCommentService;

    @PostMapping("/communities/{communityId}/posts/{postId}/comments/{commentId}/recomments")
    public ResponseEntity<ResponseBodyDto<?>> createReComment(@PathVariable("communityId") Long communityId,
                                                              @PathVariable("postId") Long postId,
                                                              @PathVariable("commentId") Long commentId,
                                                              @RequestBody CommentRequest commentRequest) {

        Long newReCommentId = reCommentService.createReComment(communityId, postId, commentId,
                commentRequest.content());

        return ResponseGenerator.success(SuccessMessage.CREATE_RE_COMMENT_SUCCESS, newReCommentId);
    }

    @GetMapping("/communities/{communityId}/posts/{postId}/comments/{commentId}/recomments")
    public ResponseEntity<ResponseBodyDto<?>> getReComments(@PathVariable("communityId") Long communityId,
                                                            @PathVariable("postId") Long postId,
                                                            @PathVariable("commentId") Long commentId) {
        List<ReCommentResponse> findReComments = reCommentService.getReComments(commentId);

        return ResponseGenerator.success(SuccessMessage.GET_RE_COMMENT_SUCCESS, findReComments);
    }
}
