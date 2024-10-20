package com.cheering.report.commentReport;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentReportController {
    private final CommentReportService commentReportService;

    // 댓글 신고
    @PostMapping("/comments/{commentId}/reports")
    ResponseEntity<?> reportComment (@PathVariable("commentId") Long commentId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        commentReportService.reportComment(commentId, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.CREATED, "신고 완료", null));
    }
}
