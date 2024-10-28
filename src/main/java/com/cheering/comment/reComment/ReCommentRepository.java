package com.cheering.comment.reComment;

import java.util.List;

import com.cheering.comment.Comment;
import com.cheering.fan.Fan;
import com.cheering.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReCommentRepository extends JpaRepository<ReComment, Long> {
    @Query("SELECT r FROM ReComment r WHERE r.comment = :comment AND r.isHide = false AND r.id NOT IN (SELECT rr.reComment.id FROM ReCommentReport rr WHERE rr.writer = :fan AND rr.reComment.id IS NOT NULL) AND r.writer NOT IN (SELECT b.to FROM Block b WHERE b.from = :fan) ORDER BY r.createdAt ASC")
    List<ReComment> findByComment(@Param("comment") Comment comment, @Param("fan") Fan curFan);

    @Query("SELECT COUNT(re) FROM ReComment re WHERE re.comment.id=:commentId AND re.isHide = false")
    Long countByCommentId(@Param("commentId") Long id);

    @Query("SELECT COUNT(re) FROM ReComment re WHERE re.comment.post = :post AND re.isHide = false AND re.comment.isHide = false")
    Long countByPost(@Param("post") Post post);
}
