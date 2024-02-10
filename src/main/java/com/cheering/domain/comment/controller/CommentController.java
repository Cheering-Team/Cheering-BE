package com.cheering.domain.comment.controller;

import com.cheering.domain.comment.dto.CommentResponse;
import com.cheering.domain.comment.service.CommentService;
import com.cheering.global.constant.SuccessMessage;
import com.cheering.global.dto.ResponseBodyDto;
import com.cheering.global.dto.ResponseGenerator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;

    @GetMapping("communities/{communityId}/posts/{postId}/comments")
    public ResponseEntity<ResponseBodyDto<?>> getComments(@PathVariable("communityId") Long communityId,
                                                          @PathVariable("postId") Long postId) {

        List<CommentResponse> findComments = commentService.getComments(communityId, postId);
        return ResponseGenerator.success(SuccessMessage.GET_COMMENT_SUCCESS, findComments);
    }

    @PostMapping("communities/{communityId}/posts/{postId}/comments")
    public ResponseEntity<ResponseBodyDto<?>> createComment(@PathVariable("communityId") Long communityId,
                                                            @PathVariable("postId") Long postId,
                                                            @RequestParam("content") String content
    ) {
        Long newCommentId = commentService.createComment(communityId, postId, content);

        return ResponseGenerator.success(SuccessMessage.CREATE_COMMENT_SUCCESS, newCommentId);
    }
}
