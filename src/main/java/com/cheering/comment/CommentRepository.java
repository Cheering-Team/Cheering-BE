package com.cheering.comment;

import com.cheering.community.relation.Fan;
import com.cheering.post.Post;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post = :post AND c.isHide = false AND c.id NOT IN (SELECT cr.comment.id FROM CommentReport cr WHERE cr.writer = :fan AND cr.comment.id IS NOT NULL) AND c.writer NOT IN (SELECT b.to FROM Block b WHERE b.from = :fan) ORDER BY c.createdAt ASC")
    Page<Comment> findByPost(@Param("post") Post post, @Param("fan") Fan fan, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post = :post AND c.isHide = false")
    Long countByPost(@Param("post") Post post);

    List<Comment> findByPost(Post post);

    @Query(value = "SELECT * FROM comment_tb WHERE post_id = :postId ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Comment> findRandomComment(@Param("postId") Long postId);
}
