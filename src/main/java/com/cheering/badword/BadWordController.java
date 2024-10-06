package com.cheering.badword;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BadWordController {
    private final BadWordService badWordService;

    @PostMapping("/badwords")
    public ResponseEntity<?> addBadWord(@RequestBody BadWordRequest.AddBadWordDTO requestDTO) {
        badWordService.addBadWord(requestDTO.word());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "추가하였습니다.", null));
    }
}
