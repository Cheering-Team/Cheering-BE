package com.cheering.report.postReport;

import com.cheering.BaseTimeEntity;
import com.cheering.player.relation.PlayerUser;
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

    @Column
    private Long userId;

    @Column(length = 2000)
    private String reportContent;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "player_user_id")
    private PlayerUser playerUser;

    @Builder
    public PostReport(Long reportId, Post post, PlayerUser playerUser, Long userId, String reportContent) {
        this.id = reportId;
        this.post = post;
        this.playerUser = playerUser;
        this.userId = userId;
        this.reportContent = reportContent;
    }
}
