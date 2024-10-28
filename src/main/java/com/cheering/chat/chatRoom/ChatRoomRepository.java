package com.cheering.chat.chatRoom;

import com.cheering.player.Player;
import com.cheering.fan.Fan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.communityId = :communityId AND cr.type = 'OFFICIAL'")
    List<ChatRoom> findOfficialByCommunityId(@Param("communityId") Long communityId);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.communityId = :communityId AND cr.type = 'PUBLIC' AND cr.manager NOT IN (SELECT b.to FROM Block b WHERE b.from = :fan)")
    List<ChatRoom> findPublicByCommunityId(@Param("communityId") Long communityId, @Param("fan") Fan fan);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.communityId IN :communityIds AND cr.type = 'OFFICIAL'")
    List<ChatRoom> findOfficialByCommunityIdsIn(@Param("communityIds") List<Long> communityIds);
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.communityId IN :communityIds AND cr.type = 'PUBLIC'")
    List<ChatRoom> findPublicByCommunityIdsIn(@Param("communityIds") List<Long> communityIds);

    Optional<ChatRoom> findByCommunityId(Long id);
}
