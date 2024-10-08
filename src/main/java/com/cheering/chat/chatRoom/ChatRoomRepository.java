package com.cheering.chat.chatRoom;

import com.cheering.player.Player;
import com.cheering.player.relation.PlayerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.player = :player AND cr.type = 'OFFICIAL'")
    List<ChatRoom> findOfficialByPlayer(@Param("player") Player player);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.player = :player AND cr.type = 'PUBLIC' AND cr.creator NOT IN (SELECT b.to FROM Block b WHERE b.from = :playerUser)")
    List<ChatRoom> findPublicByPlayer(@Param("player") Player player, @Param("playerUser")PlayerUser playerUser);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.player IN :players AND cr.type = 'OFFICIAL'")
    List<ChatRoom> findOfficialByPlayerIn(@Param("players") List<Player> players);
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.player IN :players AND cr.type = 'PUBLIC'")
    List<ChatRoom> findPublicByPlayerIn(@Param("players") List<Player> players);

    Optional<ChatRoom> findByName(String koreanName);
}
