package com.cheering.fan;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import com.cheering.user.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FanController {
    private final FanService fanService;

    // 팬 정보 조회
    @GetMapping("/fans/{fanId}")
    public ResponseEntity<?> getFanInfo(@PathVariable("fanId") Long fanId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "팬 정보 조회 완료", fanService.getFanInfo(fanId, userDetails.getUser())));
    }

    // 팬 게시글 조회
    @GetMapping("/fans/{fanId}/posts")
    public ResponseEntity<?> getFanPosts(@PathVariable("fanId") Long fanId, @RequestParam int page, @RequestParam int size, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "팬 게시글 조회 완료", fanService.getFanPosts(fanId, pageable, customUserDetails.getUser())));
    }

    // 팬 이미지 변경
    @PutMapping("/fans/{fanId}/image")
    public ResponseEntity<?> updateFanImage(@PathVariable("fanId") Long fanId, @RequestParam String type, @RequestPart(value = "image", required = false) MultipartFile image) {
        fanService.updateFanImage(fanId, type, image);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "이미지 변경 완료", null));
    }

    // 팬 이름 변경
    @PutMapping("/fans/{fanId}/name")
    public ResponseEntity<?> updateFanName(@PathVariable("fanId") Long fanId, @RequestParam String type, @RequestBody UserRequest.NameDTO requestDTO) {
        fanService.updateFanName(fanId, type, requestDTO);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "이름 변경 완료", null));
    }
}
