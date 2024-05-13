package com.cheering.comment;

import com.cheering.comment.Comment;
import com.cheering.comment.ReComment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReCommentRepository extends JpaRepository<ReComment, Long> {
    List<ReComment> findByComment(Comment comment);
}
