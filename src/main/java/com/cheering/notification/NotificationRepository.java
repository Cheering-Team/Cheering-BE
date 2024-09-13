package com.cheering.notification;

import com.cheering.player.relation.PlayerUser;
import com.cheering.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("DELETE FROM Notification n WHERE n.post = :post AND n.from = :from AND n.type =: type")
    void deleteLikeByPostAndFrom(@Param("post") Post post, @Param("from") PlayerUser curPlayerUser, @Param("type") String type);
}
