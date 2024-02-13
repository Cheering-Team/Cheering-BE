package com.cheering.domain.comment.repository;

import com.cheering.domain.comment.domain.Comment;
import com.cheering.domain.comment.domain.ReComment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReCommentRepository extends JpaRepository<ReComment, Long> {
    List<ReComment> findByComment(Comment comment);
}
