package com.cheering.post.PostImage;

import com.cheering.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPostId(Long postId);

    void deleteByPostIn(List<Post> posts);
}
