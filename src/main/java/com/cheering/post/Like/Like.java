package com.cheering.post.Like;

import com.cheering.cheer.Cheer;
import com.cheering.comment.Comment;
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

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "target_type", nullable = false)
    private String targetType;

    @Builder
    public Like(Long likeId, Fan fan, Long targetId, String targetType) {
        this.id = likeId;
        this.fan = fan;
        this.targetId = targetId;
        this.targetType = targetType;
    }

}
