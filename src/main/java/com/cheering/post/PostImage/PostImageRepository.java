package com.cheering.post.PostImage;

import com.cheering.community.relation.Fan;
import com.cheering.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPost(Post post);
    @Query("SELECT pi FROM PostImage pi WHERE pi.post.writer = :fan")
    List<PostImage> findByFan(@Param("fan") Fan fan);
}
