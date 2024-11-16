package com.cheering.post;


import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    // 게시글 작성
    @PostMapping("/communities/{communityId}/posts")
    public ResponseEntity<?> writePost(@PathVariable("communityId") Long communityId,
                                       @RequestParam(value = "content", required = false) String content,
                                       @RequestParam(value = "images", required = false) List<MultipartFile> images,
                                       @RequestParam(value = "widthDatas", required = false) List<Integer> widthDatas,
                                       @RequestParam(value = "heightDatas", required = false) List<Integer> heightDatas,
                                       @RequestParam(value = "tags", required = false) List<String> tags,
                                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "게시글 작성 완료", postService.writePost(communityId, content, images, widthDatas, heightDatas, tags, customUserDetails.getUser())));
    }

    // 커뮤니티 게시글 불러오기 (무한 스크롤)
    @GetMapping("/communities/{communityId}/posts")
    public ResponseEntity<?> getPosts(@PathVariable("communityId") Long communityId, @RequestParam(required = false) String tag, @RequestParam int page, @RequestParam int size, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "게시글 목록 조회 완료", postService.getPosts(communityId, tag, pageable, customUserDetails.getUser())));
    }

    // 특정 게시글 조회
    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getPostById(@PathVariable("postId") Long postId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "게시글 조회 완료", postService.getPostById(postId, customUserDetails.getUser())));
    }

    // 좋아요 토글
    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<?> toggleLike(@PathVariable("postId") Long postId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        PostResponse.LikeResponseDTO likeResponseDTO = postService.toggleLike(postId,customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, likeResponseDTO.isLike() ? "좋아요 완료" : "좋아요 취소", likeResponseDTO));
    }

    // 게시글 수정
    @PutMapping("/posts/{postId}")
    public ResponseEntity<?> editPost(@PathVariable("postId") Long postId,
                                      @RequestParam(value = "content", required = false) String content,
                                      @RequestParam(value = "images", required = false) List<MultipartFile> images,
                                      @RequestParam(value = "widthDatas", required = false) List<Integer> widthDatas,
                                      @RequestParam(value = "heightDatas", required = false) List<Integer> heightDatas,
                                      @RequestParam(value = "tags", required = false) List<String> tags,
                                      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        postService.editPost(postId, content, images, widthDatas, heightDatas, tags, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "게시글 수정 완료", null));
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable("postId") Long postId,  @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        postService.deletePost(postId, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "게시글을 삭제하였습니다.", null));
    }

    // 인기 게시글 조회 (무한 스크롤)
    @GetMapping("/posts")
    public ResponseEntity<?> getMyHotPosts(@RequestParam int page, @RequestParam int size, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "인기 게시글 조회 완료", postService.getMyHotPosts(pageable, customUserDetails.getUser())));
    }
}
