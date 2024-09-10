package com.cheering.comment;

import static jakarta.persistence.FetchType.LAZY;

import com.cheering.BaseTimeEntity;
import com.cheering.community.UserCommunityInfo;
import com.cheering.player.relation.PlayerUser;
import com.cheering.post.Post;
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

    @Builder
    public Comment(Long commentId, String content, Post post, PlayerUser playerUser) {
        this.id = commentId;
        this.content = content;
        this.post = post;
        this.playerUser = playerUser;
    }
}
