package com.cheering.chat.session;

import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.player.relation.PlayerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    Optional<ChatSession> findByChatRoomAndPlayerUser(ChatRoom chatRoom, PlayerUser playerUser);

    @Query("SELECT COUNT(c) FROM ChatSession c WHERE c.chatRoom.id = :chatRoomId")
    Integer countByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    @Modifying
    @Query("DELETE FROM ChatSession c WHERE c.chatRoom.id = :chatRoomId AND c.sessionId = :sessionId")
    void deleteByChatRoomIdAndSessionId(@Param("chatRoomId") Long chatRoomId, @Param("sessionId") String sessionId);

    @Query("SELECT c FROM ChatSession c WHERE c.chatRoom = :chatRoom AND c.sessionId = :sessionId")
    ChatSession findByChatRoomAndSessionId(@Param("chatRoom") ChatRoom chatRoom, @Param("sessionId") String sessionId);

    List<ChatSession> findByChatRoom(ChatRoom chatRoom);

    @Modifying
    @Query("DELETE FROM ChatSession c WHERE c.chatRoom.creator = :creator AND c.playerUser = :curPlayerUser")
    void deleteByChatRoomCreatorAndCurPlayerUser(@Param("creator") PlayerUser creator, @Param("curPlayerUser") PlayerUser curPlayerUser);
}
