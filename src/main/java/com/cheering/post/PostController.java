package com.cheering.post;


import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    @PostMapping("/players/{playerId}/posts")
    public ResponseEntity<?> writePost(@PathVariable("playerId") Long playerId,
                                       @RequestParam(value = "content", required = false) String content,
                                       @RequestParam(value = "images", required = false) List<MultipartFile> images,
                                       @RequestParam(value = "tags", required = false) List<String> tags,
                                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "게시글이 작성되었습니다.", postService.writePost(playerId, content, images, tags, customUserDetails.getUser())));
    }

    @GetMapping("/players/{playerId}/posts")
    public ResponseEntity<?> getPosts(@PathVariable("playerId") Long playerId, @RequestParam(required = false) String tag, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "게시글 목록을 불러왔습니다.", postService.getPosts(playerId, tag, customUserDetails.getUser())));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getPostById(@PathVariable("postId") Long postId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "게시글을 불러왔습니다.", postService.getPostById(postId, customUserDetails.getUser())));
    }

    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<?> toggleLike(@PathVariable("postId") Long postId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        boolean isLike = postService.toggleLike(postId, customUserDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, isLike ? "게시글에 좋아요를 눌렀습니다." : "게시글에 좋아요를 취소했습니다.", null));
    }
}

//    @PostMapping("/communities/{communityId}/posts")
//    public ResponseEntity<ResponseBodyDto<?>> createPost(@PathVariable("communityId") Long communityId,
//                                                         @RequestParam("content") String content,
//                                                         @RequestPart(value = "files", required = false) List<MultipartFile> files) {
//        Long createPostId = postService.createPost(communityId, content, files);
//
//        return ResponseGenerator.success(SuccessMessage.CREATE_POST_SUCCESS, createPostId);
//    }
//
//    @GetMapping("/communities/{communityId}/posts")
//    public ResponseEntity<ResponseBodyDto<?>> getPosts(@PathVariable("communityId") Long communityId,
//                                                       @RequestParam("type") String type) {
//        if ("PLAYER".equals(type)) {
//            List<PostResponse> playerPosts = postService.getPlayerPosts(communityId);
//            return ResponseGenerator.success(SuccessMessage.GET_POSTS_SUCCESS, playerPosts);
//        }
//
//        if ("USER".equals(type)) {
//            List<PostResponse> userPosts = postService.getUserPosts(communityId);
//            return ResponseGenerator.success(SuccessMessage.GET_POSTS_SUCCESS, userPosts);
//        }
//
//        if ("TEAM".equals(type)) {
//            List<PostResponse> teamPosts = postService.getTeamPosts(communityId);
//            return ResponseGenerator.success(SuccessMessage.GET_POSTS_SUCCESS, teamPosts);
//        }
//
//        return ResponseGenerator.fail(ExceptionMessage.INVALID_WRITER_TYPE, null);
//    }
//
//    @GetMapping("/communities/{communityId}/posts/{postId}")
//    public ResponseEntity<ResponseBodyDto<?>> detailPost(@PathVariable("communityId") Long communityId,
//                                                         @PathVariable("postId") Long postId) {
//        PostResponse findPostResponse = postService.detailPost(communityId, postId);
//
//        return ResponseGenerator.success(SuccessMessage.DETAIL_POST_SUCCESS, findPostResponse);
//    }
//
//    @GetMapping("/communities/posts")
//    public ResponseEntity<ResponseBodyDto<?>> getUserCommunityPosts() {
//
//        List<PostResponse> findPostResponse = postService.getUserCommunityPosts();
//
//        return ResponseGenerator.success(SuccessMessage.GET_POSTS_SUCCESS, findPostResponse);
//    }
//
//
//    @PostMapping("/communities/{communityId}/posts/{postId}/like")
//    public ResponseEntity<ResponseBodyDto<?>> toggleInteresting(@PathVariable("communityId") Long communityId,
//                                                                @PathVariable("postId") Long postId) {
//
//        BooleanType toggleResult = postService.toggleInteresting(communityId, postId);
//        if (BooleanType.TRUE.equals(toggleResult)) {
//            return ResponseGenerator.success(SuccessMessage.LIKE_SUCCESS, null);
//        }
//
//        return ResponseGenerator.success(SuccessMessage.LIKE_CANCEL_SUCCESS, null);
//    }
//}
