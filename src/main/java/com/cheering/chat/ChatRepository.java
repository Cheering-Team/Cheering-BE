package com.cheering.chat;

import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.player.relation.PlayerUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("SELECT c FROM Chat c WHERE c.chatRoom.id = :chatRoomId ORDER BY c.createdAt DESC")
    Page<Chat> findByChatRoomId(@Param("chatRoomId") Long chatRoomId, Pageable pageable);

    @Query(value = "SELECT * FROM chat_tb WHERE DATE_TRUNC('minute', created_at) = DATE_TRUNC('minute', CURRENT_TIMESTAMP) AND chat_room_id = :chatRoomId AND player_user_id = :writerId", nativeQuery = true)
    Optional<Chat> findByChatRoomAndWriterAndCreatedAtMinute(@Param("chatRoomId") Long chatRoomId, @Param("writerId") Long writerId);
}
