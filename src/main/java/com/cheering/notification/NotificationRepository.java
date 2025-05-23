package com.cheering.notification;

import com.cheering.comment.Comment;
import com.cheering.comment.reComment.ReComment;
import com.cheering.fan.Fan;
import com.cheering.post.Post;
import com.cheering.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.post = :post AND n.from = :from AND n.type = :type")
    void deleteLikeByPostAndFrom(@Param("post") Post post, @Param("from") Fan curFan, @Param("type") NotificaitonType type);


    @Query("SELECT n FROM Notification n WHERE n.to.user = :user AND n.from NOT IN (SELECT b.to FROM Block b WHERE b.from.user = :user) ORDER BY n.createdAt DESC")
    Page<Notification> findByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM Notification n WHERE n.to.user = :user AND n.isRead = false")
    boolean isUnreadByUser(@Param("user") User user);

    void deleteByCreatedAtBefore(LocalDateTime oneMonthAgo);
}
