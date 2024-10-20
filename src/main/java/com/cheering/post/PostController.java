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
                                       @RequestParam(value = "tags", required = false) List<String> tags,
                                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "게시글 작성 완료", postService.writePost(communityId, content, images, tags, customUserDetails.getUser())));
    }

    // 커뮤니티 게시글 불러오기 (무한 스크롤) (id = 0 -> 내가 모든 커뮤니티 게시글)
    @GetMapping("/communities/{communityId}/posts")
    public ResponseEntity<?> getPosts(@PathVariable("communityId") Long communityId, @RequestParam String type, @RequestParam(required = false) String tag, @RequestParam int page, @RequestParam int size, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "게시글 목록 조회 완료", postService.getPosts(communityId, type, tag, pageable, customUserDetails.getUser())));
    }

    // 특정 게시글 조회
    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getPostById(@PathVariable("postId") Long postId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "게시글 조회 완료", postService.getPostById(postId, customUserDetails.getUser())));
    }

    // 좋아요 토글
    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<?> toggleLike(@PathVariable("postId") Long postId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        boolean isLike = postService.toggleLike(postId, customUserDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, isLike ? "좋아요 완료" : "좋아요 취소", null));
    }

    // 게시글 수정
    @PutMapping("/posts/{postId}")
    public ResponseEntity<?> editPost(@PathVariable("postId") Long postId,
                                      @RequestParam(value = "content", required = false) String content,
                                      @RequestParam(value = "images", required = false) List<MultipartFile> images,
                                      @RequestParam(value = "tags", required = false) List<String> tags,
                                      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        postService.editPost(postId, content, images, tags, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "게시글 수정 완료", null));
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable("postId") Long postId,  @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        postService.deletePost(postId, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "게시글을 삭제하였습니다.", null));
    }

    // 데일리 작성
    @PostMapping("/communities/{communityId}/dailys")
    public ResponseEntity<?> writeDaily(@PathVariable("communityId") Long communityId, @RequestBody PostRequest.PostContentDTO requestDTO, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        postService.writeDaily(communityId, requestDTO, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "작성 완료", null));
    }

    // 특정 날짜 데일리 조회
    @GetMapping("/communities/{communityId}/dailys")
    public ResponseEntity<?> getDailys(@PathVariable("communityId") Long communityId, @RequestParam("date") String dateString, @RequestParam int page, @RequestParam int size, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "데일리 목록 조회 완료", postService.getDailys(communityId, dateString, pageable, customUserDetails.getUser())));
    }

    // 데일리 수정
    @PutMapping("/dailys/{dailyId}")
    public ResponseEntity<?> editDaily(@PathVariable("dailyId") Long dailyId, @RequestBody PostRequest.PostContentDTO requestDTO, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        postService.editDaily(dailyId, requestDTO, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "수정 완료", null));
    }

    // 데일리 삭제
    @DeleteMapping("/dailys/{dailyId}")
    public ResponseEntity<?> deleteDaily(@PathVariable("dailyId") Long dailyId) {
        postService.deleteDaily(dailyId);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "삭제 완료", null));
    }

    // 데일리 유무 조회
    @GetMapping("/communities/{communityId}/dailys/exist")
    public ResponseEntity<?> getDailyExist(@PathVariable("communityId") Long communityId) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "데일리 유무 조회 완료", postService.getDailyExist(communityId)));
    }
}
