package com.cheering.report.reCommentReport;

import com.cheering.BaseTimeEntity;
import com.cheering.comment.reComment.ReComment;
import com.cheering.player.relation.PlayerUser;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "re_comment_report_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReCommentReport extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "re_comment_report_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "re_comment_id")
    private ReComment reComment;

    @ManyToOne
    @JoinColumn(name = "player_user_id")
    private PlayerUser playerUser;

    @Builder
    public ReCommentReport(Long reportId, ReComment reComment, PlayerUser playerUser) {
        this.id = reportId;
        this.reComment = reComment;
        this.playerUser = playerUser;
    }
}
