package com.cheering.post;

import com.cheering.BaseTimeEntity;
import com.cheering.comment.Comment;
import com.cheering.notification.Notification;
import com.cheering.fan.Fan;
import com.cheering.post.Like.Like;
import com.cheering.post.PostImage.PostImage;
import com.cheering.post.relation.PostTag;
import com.cheering.report.postReport.PostReport;
import jakarta.persistence.*;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    // 누적 신고 시, 일시 숨겨짐
    @Column(nullable = false)
    private Boolean isHide = false;

    @ManyToOne
    @JoinColumn(name = "writer_id", nullable = false)
    private Fan writer;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostTag> postTags = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostImage> postImages = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostReport> postReports = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Notification> notifications = new ArrayList<>();

    @Builder
    public Post(Long postId, String content, Fan writer) {
        this.id = postId;
        this.content = content;
        this.writer = writer;
    }
}
