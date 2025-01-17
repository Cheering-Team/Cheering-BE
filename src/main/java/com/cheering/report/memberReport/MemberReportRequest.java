package com.cheering.report.memberReport;

public class MemberReportRequest {

    public record MeetMemberReportRequest(
            Long meetId,
            Long writerId,
            Long reportedFanId,
            String reason
    ) {}
}
