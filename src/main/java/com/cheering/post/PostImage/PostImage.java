package com.cheering.post.PostImage;

import com.cheering.post.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "post_image_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImage {
    @Id
    @GeneratedValue
    @Column(name = "postimage_id")
    private Long id;

    private String path;

    private int width;
    
    private int height;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public PostImage(Long postImageId, String path, int width, int height, Post post) {
        this.id = postImageId;
        this.path = path;
        this.width = width;
        this.height = height;
        this.post = post;
    }
}
