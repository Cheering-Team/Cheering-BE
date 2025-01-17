package com.cheering.report.memberReport;

import com.cheering.BaseTimeEntity;
import com.cheering.fan.Fan;
import com.cheering.meet.Meet;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "member_report_tb")
@NoArgsConstructor
@Getter
@Setter
public class MemberReport extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_report_id")
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(length = 2000)
    private String reportReason;

    @ManyToOne
    @JoinColumn(name = "writer_id")
    private Fan writer; // 신고자

    @ManyToOne
    @JoinColumn(name = "reported_id")
    private Fan reportedFan; // 신고 대상

    @ManyToOne
    @JoinColumn(name = "meet_id")
    private Meet meet;

    @Builder
    public MemberReport(Long reportId, Long userId, String reportReason, Fan writer, Fan reportedFan, Meet meet) {
        this.id = reportId;
        this.userId = userId;
        this.reportReason = reportReason;
        this.writer = writer;
        this.reportedFan = reportedFan;
        this.meet = meet;
    }
}
