package com.cheering.report.commentReport;

import com.cheering.comment.Comment;
import com.cheering.fan.Fan;
import com.cheering.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {

    @Query("select cr from CommentReport cr where cr.comment=:comment and cr.writer=:writer")
    Optional<CommentReport> findByCommentAndWriter(@Param("comment") Comment comment, @Param("writer") Fan writer);

    Long countByComment(Comment comment);

    List<CommentReport> findByComment(Comment comment);

    List<CommentReport> findByCommentIn(List<Comment> commentList);

    @Query("SELECT cr FROM CommentReport cr WHERE cr.comment.writer=:writer")
    List<CommentReport> findByWriter(@Param("writer") Fan fan);

    @Query("SELECT cr FROM CommentReport cr WHERE cr.comment.writer.user=:user")
    List<CommentReport> findByUser(@Param("user") User user);
}
