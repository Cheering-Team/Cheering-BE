package com.cheering.post;

import com.cheering.community.BooleanType;
import com.cheering._core.errors.SuccessMessage;
import com.cheering._core.errors.ResponseBodyDto;
import com.cheering._core.errors.ResponseGenerator;
import com.cheering._core.errors.ExceptionMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    @PostMapping("/communities/{communityId}/posts")
    public ResponseEntity<ResponseBodyDto<?>> createPost(@PathVariable("communityId") Long communityId,
                                                         @RequestParam("content") String content,
                                                         @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        Long createPostId = postService.createPost(communityId, content, files);

        return ResponseGenerator.success(SuccessMessage.CREATE_POST_SUCCESS, createPostId);
    }

    @GetMapping("/communities/{communityId}/posts")
    public ResponseEntity<ResponseBodyDto<?>> getPosts(@PathVariable("communityId") Long communityId,
                                                       @RequestParam("type") String type) {
        if ("PLAYER".equals(type)) {
            List<PostResponse> playerPosts = postService.getPlayerPosts(communityId);
            return ResponseGenerator.success(SuccessMessage.GET_POSTS_SUCCESS, playerPosts);
        }

        if ("USER".equals(type)) {
            List<PostResponse> userPosts = postService.getUserPosts(communityId);
            return ResponseGenerator.success(SuccessMessage.GET_POSTS_SUCCESS, userPosts);
        }

        if ("TEAM".equals(type)) {
            List<PostResponse> teamPosts = postService.getTeamPosts(communityId);
            return ResponseGenerator.success(SuccessMessage.GET_POSTS_SUCCESS, teamPosts);
        }

        return ResponseGenerator.fail(ExceptionMessage.INVALID_WRITER_TYPE, null);
    }

    @GetMapping("/communities/{communityId}/posts/{postId}")
    public ResponseEntity<ResponseBodyDto<?>> detailPost(@PathVariable("communityId") Long communityId,
                                                         @PathVariable("postId") Long postId) {
        PostResponse findPostResponse = postService.detailPost(communityId, postId);

        return ResponseGenerator.success(SuccessMessage.DETAIL_POST_SUCCESS, findPostResponse);
    }

    @GetMapping("/communities/posts")
    public ResponseEntity<ResponseBodyDto<?>> getUserCommunityPosts() {

        List<PostResponse> findPostResponse = postService.getUserCommunityPosts();

        return ResponseGenerator.success(SuccessMessage.GET_POSTS_SUCCESS, findPostResponse);
    }


    @PostMapping("/communities/{communityId}/posts/{postId}/like")
    public ResponseEntity<ResponseBodyDto<?>> toggleInteresting(@PathVariable("communityId") Long communityId,
                                                                @PathVariable("postId") Long postId) {

        BooleanType toggleResult = postService.toggleInteresting(communityId, postId);
        if (BooleanType.TRUE.equals(toggleResult)) {
            return ResponseGenerator.success(SuccessMessage.LIKE_SUCCESS, null);
        }

        return ResponseGenerator.success(SuccessMessage.LIKE_CANCEL_SUCCESS, null);
    }
}
