package com.cheering.player.relation;

import com.cheering.comment.Comment;
import com.cheering.comment.reComment.ReComment;
import com.cheering.notification.Notification;
import com.cheering.player.Player;
import com.cheering.post.Like.Like;
import com.cheering.post.Post;
import com.cheering.report.commentReport.CommentReport;
import com.cheering.report.postReport.PostReport;
import com.cheering.report.reCommentReport.ReCommentReport;
import com.cheering.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "player_user_tb")
@RequiredArgsConstructor
@Getter
@Setter
public class PlayerUser {

    @Id
    @GeneratedValue
    @Column(name = "player_user_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "uesr_id")
    private User user;

    @Column(length = 20, nullable = false, unique = true)
    private String nickname;

    @Column
    private String image;

    @OneToMany(mappedBy = "playerUser", cascade = CascadeType.REMOVE)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "playerUser", cascade = CascadeType.REMOVE)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "playerUser", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "playerUser", cascade = CascadeType.REMOVE)
    private List<ReComment> reCommentsFrom = new ArrayList<>();

    @OneToMany(mappedBy = "toPlayerUser", cascade = CascadeType.REMOVE)
    private List<ReComment> reCommentsTo = new ArrayList<>();

    @OneToMany(mappedBy = "playerUser", cascade = CascadeType.REMOVE)
    private List<ReCommentReport> reCommentReports = new ArrayList<>();

    @OneToMany(mappedBy = "playerUser", cascade = CascadeType.REMOVE)
    private List<PostReport> postReports = new ArrayList<>();

    @OneToMany(mappedBy = "playerUser", cascade = CascadeType.REMOVE)
    private List<CommentReport> commentReports = new ArrayList<>();

    @OneToMany(mappedBy = "to", cascade = CascadeType.REMOVE)
    private List<Notification> notificationsTo = new ArrayList<>();

    @OneToMany(mappedBy = "from", cascade = CascadeType.REMOVE)
    private List<Notification> notificationsFrom = new ArrayList<>();

    @Builder
    public PlayerUser(Player player, User user, String nickname, String image) {
        this.player = player;
        this.user = user;
        this.nickname = nickname;
        this.image = image;
    }
}
