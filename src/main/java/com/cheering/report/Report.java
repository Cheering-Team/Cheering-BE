package com.cheering.report;

import com.cheering.BaseTimeEntity;
import com.cheering.player.relation.PlayerUser;
import com.cheering.post.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "report_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Report extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "report_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "player_user_id")
    private PlayerUser playerUser;

    @Builder
    public Report(Long reportId, Post post, PlayerUser playerUser) {
        this.id = reportId;
        this.post = post;
        this.playerUser = playerUser;
    }
}
