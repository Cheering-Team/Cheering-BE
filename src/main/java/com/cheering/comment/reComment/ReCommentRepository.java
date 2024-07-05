package com.cheering.comment.reComment;

import com.cheering.comment.Comment;
import com.cheering.comment.reComment.ReComment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReCommentRepository extends JpaRepository<ReComment, Long> {
    @Query("SELECT r FROM ReComment r WHERE r.comment.id = :commentId ORDER BY r.createdAt ASC")
    List<ReComment> findByCommentId(@Param("commentId") Long commentId);

    @Query("SELECT COUNT(re) FROM ReComment re WHERE re.comment.id=:commentId")
    Long countByCommentId(@Param("commentId") Long id);
}
