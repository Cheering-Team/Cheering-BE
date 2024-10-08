package com.cheering.comment.reComment;

import java.util.List;

import com.cheering.player.relation.PlayerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReCommentRepository extends JpaRepository<ReComment, Long> {
    @Query("SELECT r FROM ReComment r WHERE r.comment.id = :commentId AND r.isHide = false AND r.id NOT IN (SELECT rr.reComment.id FROM ReCommentReport rr WHERE rr.playerUser = :playerUser) AND r.playerUser NOT IN (SELECT b.to FROM Block b WHERE b.from = :playerUser) ORDER BY r.createdAt ASC")
    List<ReComment> findByCommentId(@Param("commentId") Long commentId, @Param("playerUser") PlayerUser curPlayerUser);

    @Query("SELECT COUNT(re) FROM ReComment re WHERE re.comment.id=:commentId AND re.isHide = false")
    Long countByCommentId(@Param("commentId") Long id);

    @Query("SELECT COUNT(re) FROM ReComment re WHERE re.comment.post.id = :postId AND re.isHide = false AND re.comment.isHide = false")
    Long countByPostId(@Param("postId") Long postId);
}
