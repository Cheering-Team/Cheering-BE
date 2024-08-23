package com.cheering.report.commentReport;

import com.cheering.report.postReport.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {

    @Query("select cr from CommentReport cr where cr.comment.id=:commentId and cr.playerUser.id=:playerUserId")
    Optional<CommentReport> findByCommentIdAndPlayerUserId (@Param("commentId") Long commentId, @Param("playerUserId") Long playerUserId);

    @Query("SELECT COUNT(cr) FROM CommentReport cr WHERE cr.comment.id = :commentId")
    Long countByCommentId(@Param("commentId") Long commentId);
}
