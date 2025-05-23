package com.cheering.notification;

import com.cheering.BaseTimeEntity;
import com.cheering.comment.Comment;
import com.cheering.comment.reComment.ReComment;
import com.cheering.fan.Fan;
import com.cheering.meet.Meet;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificaitonType type;

    @Column
    private Boolean isRead;

    @ManyToOne
    @JoinColumn(name = "from_id", nullable = false)
    private Fan from;

    @ManyToOne
    @JoinColumn(name = "to_id", nullable = false)
    private Fan to;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "re_comment_id")
    private ReComment reComment;

    @ManyToOne
    @JoinColumn(name = "meet_id")
    private Meet meet;

    @Column
    private String meetName;

    public Notification(NotificaitonType type, Fan to, Fan from, Post post) {
        this.type = type;
        this.to = to;
        this.from = from;
        this.post =post;
        this.isRead = false;
    }

    public Notification(NotificaitonType type, Fan to, Fan from, Post post, Comment comment) {
        this.type = type;
        this.to = to;
        this.from = from;
        this.post = post;
        this.comment = comment;
        this.isRead = false;
    }

    public Notification(NotificaitonType type, Fan to, Fan from, Post post, ReComment reComment) {
        this.type = type;
        this.to = to;
        this.from = from;
        this.post = post;
        this.reComment = reComment;
        this.isRead = false;
    }

    public Notification(NotificaitonType type, Fan to, Fan from, Meet meet) {
        this.type = type;
        this.to = to;
        this.from = from;
        this.meet = meet;
        this.isRead = false;
    }
}
