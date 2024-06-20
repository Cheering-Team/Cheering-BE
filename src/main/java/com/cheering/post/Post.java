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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_tag", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tag")
    @Enumerated(EnumType.STRING)
    private Set<Tag> tags = new HashSet<>();

//    @OneToMany(mappedBy = "post")
//    private List<ImageFile> files;
//
//    @OneToMany(mappedBy = "post")
//    private List<Comment> comments = new ArrayList<>();
//
//    @OneToMany(mappedBy = "post")
//    private List<Interesting> likes = new ArrayList<>();

    @Builder
    public Post(Long postId, String content, PlayerUser playerUser, Set<Tag> tags){
        this.id = postId;
        this.content = content;
        this.playerUser = playerUser;
        this.tags = tags;
    }
}
