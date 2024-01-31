package com.cheering.domain.post.repository;

import com.cheering.domain.post.domain.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByCommunityAndPlayer(Long communityId, Long writerId);

    List<Post> findByCommunityAndUser(Long communityId, Long writerId);
}
