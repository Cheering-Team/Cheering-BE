package com.cheering.domain.post.controller;

import com.cheering.domain.post.dto.PostResponse;
import com.cheering.domain.post.service.PostService;
import com.cheering.global.constant.SuccessMessage;
import com.cheering.global.dto.ResponseBodyDto;
import com.cheering.global.dto.ResponseGenerator;
import com.cheering.global.exception.constant.ExceptionMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    @GetMapping("/communities/{communityId}/posts")
    public ResponseEntity<ResponseBodyDto<?>> getPosts(@PathVariable("communityId") Long communityId,
                                                       @RequestParam(value = "writer", required = false) Long writerId,
                                                       @RequestParam("type") String type) {
        if ("PLAYER".equals(type)) {
            List<PostResponse> playerPosts = postService.getPlayerPosts(communityId, writerId);
            return ResponseGenerator.success(SuccessMessage.GET_POSTS_SUCCESS, playerPosts);
        }

        if ("USER".equals(type)) {
            List<PostResponse> userPosts = postService.getUserPosts(communityId);
            return ResponseGenerator.success(SuccessMessage.GET_POSTS_SUCCESS, userPosts);
        }

        if ("TEAM".equals(type)) {
            List<PostResponse> teamPosts = postService.getTeamPosts(communityId, writerId);
            return ResponseGenerator.success(SuccessMessage.GET_POSTS_SUCCESS, teamPosts);
        }

        return ResponseGenerator.fail(ExceptionMessage.INVALID_WRITER_TYPE, null);
    }
}
