package com.cheering.post.Like;

import com.cheering.player.Player;
import com.cheering.player.relation.PlayerUser;
import com.cheering.post.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "like_tb")
@Getter
public class Like {
    @Id
    @GeneratedValue
    @Column(name = "like_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_user_id")
    private PlayerUser playerUser;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public Like(Long likeId, PlayerUser playerUser, Post post) {
        this.id = likeId;
        this.playerUser = playerUser;
        this.post = post;
    }

}
