package com.cheering.notice;

import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NoticeController {
    private final NoticeService noticeService;

    // 공지사항 목록 불러오기
    @GetMapping("/notices")
    public ResponseEntity<?> getNotices() {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "공지 목록 조회 완료", noticeService.getNotices()));
    }

    // 공지사항 불러오기
    @GetMapping("/notices/{noticeId}")
    public ResponseEntity<?> getNoticeById(@PathVariable("noticeId") Long noticeId) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "공지 조회 완료", noticeService.getNoticeById(noticeId)));
    }
}
