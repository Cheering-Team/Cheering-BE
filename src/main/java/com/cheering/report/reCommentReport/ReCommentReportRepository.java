package com.cheering.report.reCommentReport;

import com.cheering.comment.Comment;
import com.cheering.comment.reComment.ReComment;
import com.cheering.community.relation.Fan;
import com.cheering.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReCommentReportRepository extends JpaRepository<ReCommentReport, Long> {
    @Query("select rr from ReCommentReport rr where rr.reComment=:reComment and rr.writer=:writer")
    Optional<ReCommentReport> findByReCommentAndWriter(@Param("reComment") ReComment reComment, @Param("writer") Fan writer);

    Long countByReComment(ReComment reComment);

    List<ReCommentReport> findByReComment(ReComment reComment);

    @Query("SELECT rr FROM ReCommentReport rr WHERE rr.reComment.comment=:comment")
    List<ReCommentReport> findByComment(@Param("comment") Comment comment);

    @Query("SELECT rr FROM ReCommentReport rr WHERE rr.reComment.comment IN :comments")
    List<ReCommentReport> findByCommentIn(@Param("comments") List<Comment> commentList);

    @Query("SELECT rr FROM ReCommentReport rr WHERE rr.reComment.writer=:writer")
    List<ReCommentReport> findByWriter(@Param("writer") Fan fan);

    @Query("SELECT rr FROM ReCommentReport rr WHERE rr.reComment.writer.user=:user")
    List<ReCommentReport> findByUser(@Param("user") User user);
}
