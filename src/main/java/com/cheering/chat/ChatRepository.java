package com.cheering.chat;

import com.cheering.chat.chatRoom.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("SELECT c FROM Chat c WHERE c.chatRoom.id = :chatRoomId AND c.createdAt < :cursorDate AND c.createdAt > :enterDate ORDER BY c.createdAt DESC")
    List<Chat> findByChatRoomIdAndCreatedAtBefore(Long chatRoomId, LocalDateTime cursorDate, LocalDateTime enterDate, Pageable pageable);

    @Query("SELECT c FROM Chat c WHERE c.chatRoom.id = :chatRoomId AND c.createdAt < :createdAt AND c.createdAt > :enterDate AND c.groupKey = :groupKey ORDER BY c.createdAt DESC")
    List<Chat> findByGroupKeyAndCreatedAtBefore(Long chatRoomId, LocalDateTime createdAt, LocalDateTime enterDate, String groupKey);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Chat c WHERE c.chatRoom.id = :chatRoomId AND c.createdAt < :createdAt AND c.createdAt > :enterDate")
    boolean existsByChatRoomIdAndBeforeLastChat(Long chatRoomId, LocalDateTime createdAt, LocalDateTime enterDate);

//    @Query(value = "SELECT * FROM chat_tb WHERE DATE_TRUNC('minute', created_at) = DATE_TRUNC('minute', CURRENT_TIMESTAMP) AND chat_room_id = :chatRoomId AND writer_id = :writerId", nativeQuery = true)
//    Optional<Chat> findByChatRoomAndWriterAndCreatedAtMinute(@Param("chatRoomId") Long chatRoomId, @Param("writerId") Long writerId);
}
