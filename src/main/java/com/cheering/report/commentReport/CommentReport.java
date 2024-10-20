package com.cheering.report.commentReport;

import com.cheering.BaseTimeEntity;
import com.cheering.comment.Comment;
import com.cheering.community.relation.Fan;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comment_report_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class CommentReport extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "comment_report_id")
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(length = 1000)
    private String reportContent;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "writer_id")
    private Fan writer;

    @Builder
    public CommentReport(Long reportId, Comment comment, Fan writer, Long userId, String reportContent) {
        this.id = reportId;
        this.comment = comment;
        this.writer = writer;
        this.userId = userId;
        this.reportContent = reportContent;
    }
}
