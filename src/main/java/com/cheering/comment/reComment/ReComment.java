package com.cheering.comment.reComment;

import com.cheering.BaseTimeEntity;
import com.cheering.comment.Comment;
import com.cheering.notification.Notification;
import com.cheering.player.relation.PlayerUser;
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

    @Column(length = 1000)
    private String content;

    @Column
    private Boolean isHide = false;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "player_user_id")
    private PlayerUser playerUser;

    @ManyToOne
    @JoinColumn(name = "to_player_user_id")
    private PlayerUser toPlayerUser;

    @OneToMany(mappedBy = "reComment")
    private List<ReCommentReport> reCommentReports = new ArrayList<>();

    @OneToMany(mappedBy = "reComment", cascade = CascadeType.REMOVE)
    private List<Notification> notifications = new ArrayList<>();

    @Builder
    public ReComment(Long reCommentId, String content, Comment comment, PlayerUser playerUser, PlayerUser toPlayerUser) {
        this.id = reCommentId;
        this.content = content;
        this.comment = comment;
        this.playerUser = playerUser;
        this.toPlayerUser = toPlayerUser;
    }
}
