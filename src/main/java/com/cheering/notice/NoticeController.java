package com.cheering.notice;

import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NoticeController {
    private final NoticeService noticeService;

    // 공지사항 불러오기
    @GetMapping("/notices")
    public ResponseEntity<?> getNotices() {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "공지사항을 불러왔습니다.", noticeService.getNotices()));
    }
}
