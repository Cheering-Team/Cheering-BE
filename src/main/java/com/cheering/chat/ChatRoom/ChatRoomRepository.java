package com.cheering.chat.ChatRoom;

import com.cheering.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByPlayerAndType(Player player, ChatRoomType type);

    List<ChatRoom> findByPlayerInAndType(List<Player> players, ChatRoomType type);
}
