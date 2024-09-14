package com.cheering.notification;

import com.cheering.BaseTimeEntity;
import com.cheering.comment.Comment;
import com.cheering.comment.reComment.ReComment;
import com.cheering.player.relation.PlayerUser;
import com.cheering.post.Post;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Notification extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "notification_id")
    private Long id;

    @Column
    private String type;

    @Column
    private Boolean isRead;

    @ManyToOne
    @JoinColumn(name = "to_id")
    private PlayerUser to;

    @ManyToOne
    @JoinColumn(name = "from_id")
    private PlayerUser from;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "re_comment_id")
    private ReComment reComment;

    public Notification(String type, PlayerUser to, PlayerUser from, Post post) {
        this.type = type;
        this.to = to;
        this.from = from;
        this.post =post;
        this.isRead = false;
    }

    public Notification(String type, PlayerUser to, PlayerUser from, Post post, Comment comment) {
        this.type = type;
        this.to = to;
        this.from = from;
        this.post = post;
        this.comment = comment;
        this.isRead = false;
    }

    public Notification(String type, PlayerUser to, PlayerUser from, Post post, ReComment reComment) {
        this.type = type;
        this.to = to;
        this.from = from;
        this.post = post;
        this.reComment = reComment;
        this.isRead = false;
    }
}
