package com.cheering.notification;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NotificationController {
    private final NotificationService notificationService;

    // 알림 목록 가져오기
    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications(@RequestParam int page, @RequestParam int size, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "알림을 불러왔습니다.", notificationService.getNotifications(customUserDetails.getUser(), pageable)));
    }

    // 안읽은 알림 여부 가져오기
    @GetMapping("/notifications/is-unread")
    public ResponseEntity<?> getIsUnread(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "알림 여부를 확인했습니다.", notificationService.isUnread(customUserDetails.getUser())));
    }
}
