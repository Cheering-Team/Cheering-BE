package com.cheering.post;

import com.cheering.BaseTimeEntity;
import com.cheering.comment.Comment;
import com.cheering.notification.Notification;
import com.cheering.fan.Fan;
import com.cheering.post.PostImage.PostImage;
import com.cheering.report.postReport.PostReport;
import com.cheering.vote.Vote;
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

    @Column
    private Long communityId;

    @ManyToOne
    @JoinColumn(name = "writer_id", nullable = false)
    private Fan writer;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostImage> postImages = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostReport> postReports = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Notification> notifications = new ArrayList<>();

    @OneToOne(mappedBy = "post", cascade = CascadeType.REMOVE)
    private Vote vote;

    @Builder
    public Post(Long postId, String content, Fan writer, Long communityId) {
        this.id = postId;
        this.content = content;
        this.writer = writer;
        this.communityId = communityId;
    }
}
