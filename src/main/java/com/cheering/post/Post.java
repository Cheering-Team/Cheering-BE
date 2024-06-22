package com.cheering.post;

import com.cheering.BaseTimeEntity;
import com.cheering.player.relation.PlayerUser;
import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "post_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @Column
    private String content;

    @ManyToOne
    @JoinColumn(name = "player_user_id")
    private PlayerUser playerUser;

//    @OneToMany(mappedBy = "post")
//    private List<ImageFile> files;
//
//    @OneToMany(mappedBy = "post")
//    private List<Comment> comments = new ArrayList<>();
//
//    @OneToMany(mappedBy = "post")
//    private List<Interesting> likes = new ArrayList<>();

    @Builder
    public Post(Long postId, String content, PlayerUser playerUser){
        this.id = postId;
        this.content = content;
        this.playerUser = playerUser;
    }
}
