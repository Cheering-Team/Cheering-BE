package com.cheering.report.memberReport;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import com.cheering.user.User;
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
public class MemberReportController {

    private final MemberReportService memberReportService;

    @PostMapping("/meets/{meetId}/reports")
    ResponseEntity<?> reportMember (@PathVariable("meetId") Long meetId, MemberReportRequest.MeetMemberReportRequest request, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = customUserDetails.getUser();
        memberReportService.reportMember(request, user);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.CREATED, "신고 완료", null));
    }
}
