package com.cheering.notification;

import com.cheering.comment.Comment;
import com.cheering.comment.reComment.ReComment;
import com.cheering.player.relation.PlayerUser;
import com.cheering.post.Post;
import com.cheering.report.reCommentReport.ReCommentReport;
import com.cheering.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.post = :post AND n.from = :from AND n.type = :type")
    void deleteLikeByPostAndFrom(@Param("post") Post post, @Param("from") PlayerUser curPlayerUser, @Param("type") String type);


    @Query("SELECT n FROM Notification n WHERE n.to.user = :user ORDER BY n.createdAt DESC")
    Page<Notification> findByUser(@Param("user") User user, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.comment = :comment")
    void deleteByComment(@Param("comment") Comment comment);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.reComment = :reComment")
    void deleteByReComment(@Param("reComment") ReComment reComment);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.reComment.comment.id = :commentId")
    void deleteByCommentId(@Param("commentId") Long commentId);

    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM Notification n WHERE n.to.user = :user AND n.isRead = false")
    boolean isUnreadByUser(@Param("user") User user);
}
