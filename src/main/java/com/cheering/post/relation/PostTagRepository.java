package com.cheering.post.relation;

import com.cheering.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    List<PostTag> findByPostId(Long postId);

    void deleteByPostIn(List<Post> posts);

    void deleteByPost(Post post);
}
