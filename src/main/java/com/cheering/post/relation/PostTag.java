package com.cheering.post.relation;

import com.cheering.post.Post;
import com.cheering.post.Tag.Tag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "post_tag_tb")
public class PostTag {
    @Id
    @GeneratedValue
    @Column(name = "post_tag_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Builder
    public PostTag(Long postTagId, Post post, Tag tag) {
        this.id = postTagId;
        this.post = post;
        this.tag = tag;
    }
}
