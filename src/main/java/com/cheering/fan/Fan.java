package com.cheering.fan;

import com.cheering.BaseTimeEntity;
import com.cheering.chat.Chat;
import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.chat.session.ChatSession;
import com.cheering.cheer.Cheer;
import com.cheering.comment.Comment;
import com.cheering.comment.reComment.ReComment;
import com.cheering.meet.Meet;
import com.cheering.meetfan.MeetFan;
import com.cheering.notification.Notification;
import com.cheering.post.Like.Like;
import com.cheering.post.Post;
import com.cheering.report.block.Block;
import com.cheering.report.commentReport.CommentReport;
import com.cheering.report.memberReport.MemberReport;
import com.cheering.report.postReport.PostReport;
import com.cheering.report.reCommentReport.ReCommentReport;
import com.cheering.user.User;
import com.cheering.vote.fanVote.FanVote;
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
public class Fan extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "fan_id")
    private Long id;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CommunityType type;

    @Column(length = 10, nullable = false)
    private String name;

    @Column(length = 2000)
    private String image;

    @Column(length = 15)
    private String meetName;

    @Column(length = 2000)
    private String meetImage;

    @Column
    private Long communityId;

    @Column
    private Integer communityOrder;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    @OneToMany(mappedBy = "writer", cascade = CascadeType.REMOVE)
    private List<MemberReport> memberReports = new ArrayList<>();

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

    @OneToMany(mappedBy = "writer", cascade = CascadeType.REMOVE)
    private List<Cheer> cheers = new ArrayList<>();

    @OneToMany(mappedBy = "fan", cascade = CascadeType.REMOVE)
    private List<FanVote> fanVotes = new ArrayList<>();

    @OneToMany(mappedBy = "manager", cascade = CascadeType.REMOVE)
    private List<Meet> meets = new ArrayList<>();

    @OneToMany(mappedBy = "fan", cascade = CascadeType.REMOVE)
    private List<MeetFan> meetFans = new ArrayList<>();

    @Builder
    public Fan(CommunityType type, String name, String image, Long communityId, Integer communityOrder, User user) {
        this.type = type;
        this.name = name;
        this.image = image;
        this.communityId = communityId;
        this.user = user;
        this.communityOrder = communityOrder;
    }

    public Fan(Long fanId) {
        this.id = fanId;
    }
}
