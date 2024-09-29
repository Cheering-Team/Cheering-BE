package com.cheering.comment;

import com.cheering.player.relation.PlayerUser;
import com.cheering.post.Post;
import java.util.List;

import com.cheering.report.commentReport.CommentReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.isHide = false ORDER BY c.createdAt ASC")
    Page<Comment> findByPostId(@Param("postId") Long postId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId AND c.isHide = false")
    Long countByPostId(@Param("postId") Long postId);

    List<Comment> findByPost(Post post);
}
