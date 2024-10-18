package com.cheering.post.relation;

import com.cheering.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    void deleteByPost(Post post);
    List<PostTag> findByPost(Post post);
}
