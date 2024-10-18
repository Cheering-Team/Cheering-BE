package com.cheering.comment.reComment;

import com.cheering.BaseTimeEntity;
import com.cheering.comment.Comment;
import com.cheering.notification.Notification;
import com.cheering.community.relation.Fan;
import com.cheering.report.reCommentReport.ReCommentReport;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "re_comment_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class ReComment extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "re_comment_id")
    private Long id;

    @Column(length = 1000, nullable = false)
    private String content;

    @Column(nullable = false)
    private Boolean isHide = false;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "writer_id", nullable = false)
    private Fan writer;

    @ManyToOne
    @JoinColumn(name = "to_id")
    private Fan to;

    @OneToMany(mappedBy = "reComment")
    private List<ReCommentReport> reCommentReports = new ArrayList<>();

    @OneToMany(mappedBy = "reComment", cascade = CascadeType.REMOVE)
    private List<Notification> notifications = new ArrayList<>();

    @Builder
    public ReComment(Long reCommentId, String content, Comment comment, Fan writer, Fan to) {
        this.id = reCommentId;
        this.content = content;
        this.comment = comment;
        this.writer = writer;
        this.to = to;
    }
}
