package com.cheering.domain.comment.repository;

import com.cheering.domain.comment.domain.Comment;
import com.cheering.domain.post.domain.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findCommentsByPost(Post post);
}
