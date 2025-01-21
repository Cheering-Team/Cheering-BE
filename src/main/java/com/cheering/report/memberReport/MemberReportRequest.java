package com.cheering.report.memberReport;

public class MemberReportRequest {

    public record MeetMemberReportRequest(
            Long reportedFanId,
            String reason
    ) {}
}
