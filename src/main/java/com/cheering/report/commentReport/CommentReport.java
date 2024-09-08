package com.cheering.report.commentReport;

import com.cheering.BaseTimeEntity;
import com.cheering.comment.Comment;
import com.cheering.player.relation.PlayerUser;
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

    @Column
    private Long userId;

    @Column(length = 1000)
    private String reportContent;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "player_user_id")
    private PlayerUser playerUser;

    @Builder
    public CommentReport(Long reportId, Comment comment, PlayerUser playerUser, Long userId, String reportContent) {
        this.id = reportId;
        this.comment = comment;
        this.playerUser = playerUser;
        this.userId = userId;
        this.reportContent = reportContent;
    }
}
