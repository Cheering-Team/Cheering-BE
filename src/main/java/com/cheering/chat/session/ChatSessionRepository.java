package com.cheering.chat.session;

import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.fan.Fan;
import com.cheering.user.User;
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

    @Query("SELECT c FROM ChatSession c WHERE c.chatRoom.id = :chatRoomId AND c.sessionId = :sessionId")
    Optional<ChatSession> findByChatRoomIdAndSessionId(Long chatRoomId, String sessionId);

    List<ChatSession> findByChatRoom(ChatRoom chatRoom);

    @Query("SELECT c FROM ChatSession c WHERE c.fan.user = :user")
    List<ChatSession> findByUser(User user);

    @Modifying
    @Query("DELETE FROM ChatSession c WHERE c.chatRoom.manager = :manager AND c.fan = :fan")
    void deleteByChatRoomManagerAndCurFan(@Param("manager") Fan manager, @Param("fan") Fan curFan);

    @Query("SELECT cs FROM ChatSession cs WHERE cs.chatRoom.id = :chatRoomId AND cs.fan.user = :user")
    Optional<ChatSession> findByChatRoomIdAndUser(Long chatRoomId, User user);

    @Query("SELECT cs.fan.user FROM ChatSession cs WHERE cs.chatRoom = :chatRoom AND cs.fan.id != :myId")
    List<User> findByChatRoomExceptMe(ChatRoom chatRoom, Long myId);
}
