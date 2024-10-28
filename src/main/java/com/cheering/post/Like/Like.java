package com.cheering.post.Like;

import com.cheering.fan.Fan;
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
    @JoinColumn(name = "fan_id", nullable = false)
    private Fan fan;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Builder
    public Like(Long likeId, Fan fan, Post post) {
        this.id = likeId;
        this.fan = fan;
        this.post = post;
    }

}
