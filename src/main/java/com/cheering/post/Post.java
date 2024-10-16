package com.cheering.post;

import com.cheering.BaseTimeEntity;
import com.cheering.comment.Comment;
import com.cheering.notification.Notification;
import com.cheering.player.relation.PlayerUser;
import com.cheering.post.Like.Like;
import com.cheering.post.PostImage.PostImage;
import com.cheering.post.relation.PostTag;
import com.cheering.report.postReport.PostReport;
import com.cheering.user.Role;
import jakarta.persistence.*;

import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "post_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @Column(length = 1000)
    private String content;

    @Column
    private Boolean isHide = false;

    @Column
    @Enumerated(value = EnumType.STRING)
    private PostType type;

    @ManyToOne
    @JoinColumn(name = "player_user_id")
    private PlayerUser playerUser;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostTag> postTags = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostReport> postReports = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostImage> postImages = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Notification> notifications = new ArrayList<>();

    @Builder
    public Post(Long postId, String content, PlayerUser playerUser, PostType type) {
        this.id = postId;
        this.content = content;
        this.playerUser = playerUser;
        this.type = type;
    }
}
