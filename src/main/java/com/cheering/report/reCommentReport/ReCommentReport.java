package com.cheering.report.reCommentReport;

import com.cheering.BaseTimeEntity;
import com.cheering.comment.reComment.ReComment;
import com.cheering.community.relation.Fan;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "re_comment_report_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class ReCommentReport extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "re_comment_report_id")
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(length = 1000)
    private String reportContent;

    @ManyToOne
    @JoinColumn(name = "re_comment_id")
    private ReComment reComment;

    @ManyToOne
    @JoinColumn(name = "writer_id")
    private Fan writer;

    @Builder
    public ReCommentReport(Long reportId, ReComment reComment, Fan writer, Long userId, String reportContent) {
        this.id = reportId;
        this.reComment = reComment;
        this.writer = writer;
        this.userId = userId;
        this.reportContent = reportContent;
    }
}
