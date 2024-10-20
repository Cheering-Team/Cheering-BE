package com.cheering.post.PostImage;

import com.cheering.post.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_image_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImage {
    @Id
    @GeneratedValue
    @Column(name = "post_image_id")
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PostImageType type;

    @Column(nullable = false)
    private String path;

    @Column
    private int width;

    @Column
    private int height;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public PostImage(Long postImageId, String path, int width, int height, Post post, PostImageType type) {
        this.id = postImageId;
        this.path = path;
        this.width = width;
        this.height = height;
        this.post = post;
        this.type = type;
    }
}
