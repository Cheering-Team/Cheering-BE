package com.cheering.domain.post.controller;

import com.cheering.domain.post.dto.PostResponse;
import com.cheering.domain.post.service.PostService;
import com.cheering.global.constant.SuccessMessage;
import com.cheering.global.dto.ResponseBodyDto;
import com.cheering.global.dto.ResponseGenerator;
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
                                                       @RequestParam("writer") Long writerId,
                                                       @RequestParam("type") String type) {

        List<PostResponse> posts = postService.getPosts(communityId, writerId, type);
        return ResponseGenerator.success(SuccessMessage.GET_POSTS_SUCCESS, posts);
    }
}
