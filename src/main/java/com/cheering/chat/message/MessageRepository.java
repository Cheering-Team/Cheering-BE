package com.cheering.chat.message;

import com.cheering.chat.Chat;
import com.cheering.chat.chatRoom.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChat(Chat chat);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.chat.chatRoom = :chatRoom AND m.createdAt > :lastExitTime")
    Integer countUnreadMessages(@Param("chatRoom") ChatRoom chatRoom, @Param("lastExitTime") LocalDateTime lastExitTime);

    @Query("SELECT m FROM Message m WHERE m.chat.chatRoom = :chatRoom ORDER BY m.createdAt DESC")
    List<Message> findLastMessage(@Param("chatRoom") ChatRoom chatRoom, Pageable pageable);

}
