package com.cheering.chat.chatRoom;

import com.cheering.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByPlayerAndType(Player player, ChatRoomType type);

    List<ChatRoom> findByPlayerInAndType(List<Player> players, ChatRoomType type);

    Optional<ChatRoom> findByName(String koreanName);
}
