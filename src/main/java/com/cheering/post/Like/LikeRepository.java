package com.cheering.post.Like;

import com.cheering.fan.Fan;
import com.cheering.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Long countByPost(Post post);
    @Query("select like from Like like where like.post=:post and like.fan=:fan")
    Optional<Like> findByPostAndFan(@Param("post") Post post, @Param("fan") Fan fan );
}
