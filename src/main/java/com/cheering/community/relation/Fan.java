package com.cheering.community.relation;

import com.cheering.chat.Chat;
import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.chat.session.ChatSession;
import com.cheering.comment.Comment;
import com.cheering.comment.reComment.ReComment;
import com.cheering.notification.Notification;
import com.cheering.community.Community;
import com.cheering.post.Like.Like;
import com.cheering.post.Post;
import com.cheering.report.block.Block;
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
@Table(name = "fan_tb")
@RequiredArgsConstructor
@Getter
@Setter
public class Fan {

    @Id
    @GeneratedValue
    @Column(name = "fan_id")
    private Long id;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private FanType type;

    @Column(length = 20, nullable = false, unique = true)
    private String name;

    @Column
    private String image;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    @OneToOne(mappedBy = "manager")
    private Community myCommunity;

    @OneToMany(mappedBy = "writer", cascade = CascadeType.REMOVE)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "fan", cascade = CascadeType.REMOVE)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "writer", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "writer", cascade = CascadeType.REMOVE)
    private List<ReComment> reCommentsFrom = new ArrayList<>();

    @OneToMany(mappedBy = "to", cascade = CascadeType.REMOVE)
    private List<ReComment> reCommentsTo = new ArrayList<>();

    @OneToMany(mappedBy = "writer", cascade = CascadeType.REMOVE)
    private List<PostReport> postReports = new ArrayList<>();

    @OneToMany(mappedBy = "writer", cascade = CascadeType.REMOVE)
    private List<CommentReport> commentReports = new ArrayList<>();

    @OneToMany(mappedBy = "writer", cascade = CascadeType.REMOVE)
    private List<ReCommentReport> reCommentReports = new ArrayList<>();

    @OneToMany(mappedBy = "from", cascade = CascadeType.REMOVE)
    private List<Block> blocksFrom = new ArrayList<>();

    @OneToMany(mappedBy = "to", cascade = CascadeType.REMOVE)
    private List<Block> blocksTo = new ArrayList<>();

    @OneToMany(mappedBy = "manager", cascade = CascadeType.REMOVE)
    private List<ChatRoom> chatRooms = new ArrayList<>();

    @OneToMany(mappedBy = "fan", cascade = CascadeType.REMOVE)
    private List<ChatSession> chatSessions = new ArrayList<>();

    @OneToMany(mappedBy = "writer", cascade = CascadeType.REMOVE)
    private List<Chat> chats = new ArrayList<>();

    @OneToMany(mappedBy = "to", cascade = CascadeType.REMOVE)
    private List<Notification> notificationsTo = new ArrayList<>();

    @OneToMany(mappedBy = "from", cascade = CascadeType.REMOVE)
    private List<Notification> notificationsFrom = new ArrayList<>();

    @Builder
    public Fan(Community community, FanType type, User user, String name, String image) {
        this.community = community;
        this.type = type;
        this.user = user;
        this.name = name;
        this.image = image;
    }
}
