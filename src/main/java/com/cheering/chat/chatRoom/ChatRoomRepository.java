package com.cheering.chat.chatRoom;

import com.cheering.community.Community;
import com.cheering.community.relation.Fan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.community = :community AND cr.type = 'OFFICIAL'")
    List<ChatRoom> findOfficialByCommunity(@Param("community") Community community);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.community = :community AND cr.type = 'PUBLIC' AND cr.manager NOT IN (SELECT b.to FROM Block b WHERE b.from = :fan)")
    List<ChatRoom> findPublicByCommunity(@Param("community") Community community, @Param("fan") Fan fan);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.community IN :communities AND cr.type = 'OFFICIAL'")
    List<ChatRoom> findOfficialByCommunityIn(@Param("communities") List<Community> communities);
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.community IN :communities AND cr.type = 'PUBLIC'")
    List<ChatRoom> findPublicByCommunityIn(@Param("communities") List<Community> communities);

    Optional<ChatRoom> findByName(String koreanName);
}
