package com.cheering.post;


import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    // 게시글 작성
    @PostMapping("/players/{playerId}/posts")
    public ResponseEntity<?> writePost(@PathVariable("playerId") Long playerId,
                                       @RequestParam(value = "content", required = false) String content,
                                       @RequestParam(value = "images", required = false) List<MultipartFile> images,
                                       @RequestParam(value = "tags", required = false) List<String> tags,
                                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "게시글이 작성되었습니다.", postService.writePost(playerId, content, images, tags, customUserDetails.getUser())));
    }

    // 특정 커뮤니티 게시글 불러오기 (id = 0 -> 내가 가입된 모든 커뮤니티 게시글)
    @GetMapping("/players/{playerId}/posts")
    public ResponseEntity<?> getPosts(@PathVariable("playerId") Long playerId, @RequestParam(required = false) String tag, @RequestParam int page, @RequestParam int size, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "게시글 목록을 불러왔습니다.", postService.getPosts(playerId, tag, pageable, customUserDetails.getUser())));
    }

    // 게시글 불러오기
    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getPostById(@PathVariable("postId") Long postId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "게시글을 불러왔습니다.", postService.getPostById(postId, customUserDetails.getUser())));
    }

    // 좋아요 토글
    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<?> toggleLike(@PathVariable("postId") Long postId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        boolean isLike = postService.toggleLike(postId, customUserDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, isLike ? "게시글에 좋아요를 눌렀습니다." : "게시글에 좋아요를 취소했습니다.", null));
    }

    // 게시글 수정
    @PutMapping("/posts/{postId}")
    public ResponseEntity<?> editPost(@PathVariable("postId") Long postId,
                                      @RequestParam(value = "content", required = false) String content,
                                      @RequestParam(value = "images", required = false) List<MultipartFile> images,
                                      @RequestParam(value = "tags", required = false) List<String> tags,
                                      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        postService.editPost(postId, content, images, tags, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "게시글을 수정하였습니다.", null));
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable("postId") Long postId,  @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        postService.deletePost(postId, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "게시글을 삭제하였습니다.", null));
    }

    // 데일리 작성
    @PostMapping("/players/{playerId}/dailys")
    public ResponseEntity<?> writeDaily(@PathVariable("playerId") Long playerId, @RequestBody PostRequest.PostContentDTO requestDTO, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        postService.writeDaily(playerId, requestDTO, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "작성 완료", null));
    }

    // 특정 커뮤니티 특정 날짜 데일리 불러오기
    @GetMapping("/players/{playerId}/dailys")
    public ResponseEntity<?> getDailys(@PathVariable("playerId") Long playerId, @RequestParam("date") String dateString, @RequestParam int page, @RequestParam int size, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "데일리 목록을 불러왔습니다.", postService.getDailys(playerId, dateString, pageable, customUserDetails.getUser())));
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

    // 특정 달(month)에 데일리가 있는 날짜 반환
    @GetMapping("/players/{playerId}/dailys/exist")
    public ResponseEntity<?> getDailyExist(@PathVariable("playerId") Long playerId) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "데일리 유무 로드", postService.getDailyExist(playerId)));
    }
}
