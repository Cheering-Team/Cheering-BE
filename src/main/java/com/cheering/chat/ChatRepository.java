package com.cheering.chat;

import com.cheering.chat.chatRoom.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("SELECT c FROM Chat c WHERE c.chatRoom = :chatRoom ORDER BY c.createdAt DESC")
    Page<Chat> findByChatRoom(@Param("chatRoom") ChatRoom chatRoom, Pageable pageable);

    @Query(value = "SELECT * FROM chat_tb WHERE DATE_TRUNC('minute', created_at) = DATE_TRUNC('minute', CURRENT_TIMESTAMP) AND chat_room_id = :chatRoomId AND player_user_id = :writerId", nativeQuery = true)
    Optional<Chat> findByChatRoomAndWriterAndCreatedAtMinute(@Param("chatRoomId") Long chatRoomId, @Param("writerId") Long writerId);
}
