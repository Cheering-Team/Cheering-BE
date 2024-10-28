package com.cheering.report.postReport;

import com.cheering.BaseTimeEntity;
import com.cheering.fan.Fan;
import com.cheering.post.Post;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_report_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class PostReport extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "post_report_id")
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(length = 2000)
    private String reportContent;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "writer_id")
    private Fan writer;

    @Builder
    public PostReport(Long reportId, Post post, Fan writer, Long userId, String reportContent) {
        this.id = reportId;
        this.post = post;
        this.writer = writer;
        this.userId = userId;
        this.reportContent = reportContent;
    }
}
