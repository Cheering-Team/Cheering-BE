package com.cheering.report.reCommentReport;

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
public class ReCommentReportController {
    private final ReCommentReportService reCommentReportService;

    // 답글 신고
    @PostMapping("/reComments/{reCommentId}/reports")
    ResponseEntity<?> reportReComment (@PathVariable("reCommentId") Long reCommentId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        reCommentReportService.reportReComment(reCommentId, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.CREATED, "신고 완료", null));
    }
}
