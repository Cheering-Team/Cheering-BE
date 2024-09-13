package com.cheering.comment;

import com.cheering.BaseTimeEntity;
import com.cheering.comment.reComment.ReComment;
import com.cheering.notification.Notification;
import com.cheering.player.relation.PlayerUser;
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

    @Column(length = 1000)
    private String content;

    @Column
    private Boolean isHide = false;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "player_user_id")
    private PlayerUser playerUser;

    @OneToMany(mappedBy = "comment")
    private List<CommentReport> commentReports = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE)
    private List<ReComment> reComments = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE)
    private List<Notification> notifications = new ArrayList<>();

    @Builder
    public Comment(Long commentId, String content, Post post, PlayerUser playerUser) {
        this.id = commentId;
        this.content = content;
        this.post = post;
        this.playerUser = playerUser;
    }
}
