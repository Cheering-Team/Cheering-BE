package com.cheering.report.reCommentReport;

import com.cheering.comment.Comment;
import com.cheering.comment.reComment.ReComment;
import com.cheering.player.relation.PlayerUser;
import com.cheering.report.commentReport.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReCommentReportRepository extends JpaRepository<ReCommentReport, Long> {
    @Query("select rr from ReCommentReport rr where rr.reComment.id=:reCommentId and rr.playerUser.id=:playerUserId")
    Optional<ReCommentReport> findByReCommentIdAndPlayerUserId (@Param("reCommentId") Long reCommentId, @Param("playerUserId") Long playerUserId);

    @Query("SELECT COUNT(rr) FROM ReCommentReport rr WHERE rr.reComment.id = :reCommentId")
    Long countByReCommentId(@Param("reCommentId") Long reCommentId);

    List<ReCommentReport> findByReComment(ReComment reComment);

    @Query("SELECT rr FROM ReCommentReport rr WHERE rr.reComment.comment.id=:commentId")
    List<ReCommentReport> findByCommentId(@Param("commentId") Long commentId);

    @Query("SELECT rr FROM ReCommentReport rr WHERE rr.reComment.comment IN :comments")
    List<ReCommentReport> findByCommentIn(@Param("comments") List<Comment> commentList);

    @Query("SELECT rr FROM ReCommentReport rr WHERE rr.reComment.playerUser=:playerUser")
    List<ReCommentReport> findByWriter(@Param("playerUser") PlayerUser playerUser);

    void deleteByPlayerUser(PlayerUser playerUser);
}
