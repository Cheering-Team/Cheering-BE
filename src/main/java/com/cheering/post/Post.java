package com.cheering.post;

import com.cheering.BaseTimeEntity;
import com.cheering.comment.Comment;
import com.cheering.player.relation.PlayerUser;
import jakarta.persistence.*;

import lombok.*;

import java.util.HashSet;
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

    @ManyToOne
    @JoinColumn(name = "player_user_id")
    private PlayerUser playerUser;

    @Builder
    public Post(Long postId, String content, PlayerUser playerUser) {
        this.id = postId;
        this.content = content;
        this.playerUser = playerUser;
    }
}
