package com.cheering.chat.ChatRoom;

import com.cheering.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByPlayer(Player player);

    List<ChatRoom> findByPlayerIn(List<Player> players);
}
