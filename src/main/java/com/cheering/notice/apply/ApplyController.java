package com.cheering.notice.apply;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApplyController {
    private final ApplyService applyService;

    @PostMapping("/applies")
    public ResponseEntity<?> apply(@RequestParam(value = "field1", required = false) String field1, @RequestParam(value = "field2", required = false) String field2, @RequestParam(value = "field3", required = false) String field3, @RequestParam(value = "field4", required = false) String field4, @RequestParam(value = "image", required = false) MultipartFile image) {
        applyService.apply(field1, field2, field3, field4, image);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "신청 완료", null));
    }
}
