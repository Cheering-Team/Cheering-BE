package com.cheering.comment;

import com.cheering.BaseTimeEntity;
import com.cheering.comment.reComment.ReComment;
import com.cheering.notification.Notification;
import com.cheering.community.relation.Fan;
import com.cheering.post.Post;
import com.cheering.report.commentReport.CommentReport;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Entity
@Table(name = "comment_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @Column(length = 1000, nullable = false)
    private String content;

    // 신고 누적 시, 임시 숨겨짐
    @Column(nullable = false)
    private Boolean isHide = false;

    @ManyToOne
    @JoinColumn(name = "writer_id", nullable = false)
    private Fan writer;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE)
    private List<ReComment> reComments = new ArrayList<>();

    @OneToMany(mappedBy = "comment")
    private List<CommentReport> commentReports = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE)
    private List<Notification> notifications = new ArrayList<>();

    @Builder
    public Comment(Long commentId, String content, Post post, Fan writer) {
        this.id = commentId;
        this.content = content;
        this.post = post;
        this.writer = writer;
    }
}
