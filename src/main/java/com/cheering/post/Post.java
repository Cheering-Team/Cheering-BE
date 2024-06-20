package com.cheering.post;

import static jakarta.persistence.FetchType.LAZY;

import com.cheering.BaseTimeEntity;
import com.cheering.comment.Comment;
import com.cheering.community.UserCommunityInfo;
import com.cheering.player.Player;
import com.cheering.player.relation.PlayerUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
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
