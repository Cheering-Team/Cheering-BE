package com.cheering.chat.session;

import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.community.relation.Fan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    Optional<ChatSession> findByChatRoomAndFan(ChatRoom chatRoom, Fan fan);

    @Query("SELECT COUNT(c) FROM ChatSession c WHERE c.chatRoom = :chatRoom")
    Integer countByChatRoom(@Param("chatRoom") ChatRoom chatRoom);

    @Modifying
    @Query("DELETE FROM ChatSession c WHERE c.chatRoom.id = :chatRoomId AND c.sessionId = :sessionId")
    void deleteByChatRoomIdAndSessionId(@Param("chatRoomId") Long chatRoomId, @Param("sessionId") String sessionId);

    @Query("SELECT c FROM ChatSession c WHERE c.chatRoom = :chatRoom AND c.sessionId = :sessionId")
    ChatSession findByChatRoomAndSessionId(@Param("chatRoom") ChatRoom chatRoom, @Param("sessionId") String sessionId);

    List<ChatSession> findByChatRoom(ChatRoom chatRoom);

    @Modifying
    @Query("DELETE FROM ChatSession c WHERE c.chatRoom.manager = :manager AND c.fan = :fan")
    void deleteByChatRoomManagerAndCurFan(@Param("manager") Fan manager, @Param("fan") Fan curFan);
}
