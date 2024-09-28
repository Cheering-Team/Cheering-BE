package com.cheering.chat.ChatRoom;

import com.cheering.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT c FROM ChatRoom c WHERE c.player = :player ORDER BY CASE WHEN c.type = 'OFFICIAL' THEN 0 ELSE 1 END")
    List<ChatRoom> findByPlayer(@Param("player") Player player);

    List<ChatRoom> findByPlayerIn(List<Player> players);
}
