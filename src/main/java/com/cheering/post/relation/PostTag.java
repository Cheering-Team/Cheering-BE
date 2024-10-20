package com.cheering.post.relation;

import com.cheering.post.Post;
import com.cheering.post.Tag.Tag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "post_tag_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class PostTag {
    @Id
    @GeneratedValue
    @Column(name = "post_tag_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @Builder
    public PostTag(Long postTagId, Post post, Tag tag) {
        this.id = postTagId;
        this.post = post;
        this.tag = tag;
    }
}
