package com.cheering.domain.comment.controller;

import com.cheering.domain.comment.dto.request.CommentRequest;
import com.cheering.domain.comment.service.ReCommentService;
import com.cheering.global.constant.SuccessMessage;
import com.cheering.global.dto.ResponseBodyDto;
import com.cheering.global.dto.ResponseGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
}
