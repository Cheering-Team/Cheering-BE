package com.cheering.post.PostImage;

import com.cheering.player.relation.PlayerUser;
import com.cheering.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPostId(Long postId);
    @Query("SELECT pi FROM PostImage pi WHERE pi.post.playerUser = :playerUser")
    List<PostImage> findByPlayerUser(@Param("playerUser") PlayerUser playerUser);
    void deleteByPost(Post post);
}
