package com.cheering.chat.chatRoom;

import com.cheering.player.Player;
import com.cheering.fan.Fan;
import com.cheering.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.communityId = :communityId AND cr.type = 'OFFICIAL'")
    ChatRoom findOfficialByCommunityId(@Param("communityId") Long communityId);

    @Query("SELECT cr FROM ChatRoom cr LEFT JOIN ChatSession cs ON cr.id = cs.chatRoom.id WHERE cr.communityId = :communityId AND cr.type = 'PUBLIC' AND cr.manager NOT IN (SELECT b.to FROM Block b WHERE b.from =:fan) GROUP BY cr.id ORDER BY COUNT(cs) DESC")
    Page<ChatRoom> findPublicByCommunityIdByCount(@Param("communityId") Long communityId, @Param("fan") Fan curFan, Pageable pageable);

    @Query("SELECT cr FROM ChatRoom cr LEFT JOIN ChatSession cs ON cr.id = cs.chatRoom.id WHERE cr.communityId = :communityId AND cr.type = 'PUBLIC' AND cr.manager NOT IN (SELECT b.to FROM Block b WHERE b.from =:fan) AND (REPLACE(cr.name, ' ', '') LIKE %:name% OR REPLACE(cr.description, ' ', '') LIKE %:name%) GROUP BY cr.id ORDER BY COUNT(cs) DESC")
    Page<ChatRoom> findPublicByCommunityIdByCountWithName(@Param("communityId") Long communityId, @Param("fan") Fan curFan, @Param("name") String name, Pageable pageable);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.communityId = :communityId AND cr.type = 'PUBLIC' AND cr.manager NOT IN (SELECT b.to FROM Block b WHERE b.from =:fan)  ORDER BY cr.createdAt DESC")
    Page<ChatRoom> findPublicByCommunityIdByCreatedAt(@Param("communityId") Long communityId, @Param("fan") Fan curFan, Pageable pageable);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.communityId = :communityId AND cr.type = 'PUBLIC' AND cr.manager NOT IN (SELECT b.to FROM Block b WHERE b.from =:fan) AND (REPLACE(cr.name, ' ', '') LIKE %:name% OR REPLACE(cr.description, ' ', '') LIKE %:name%) ORDER BY cr.createdAt DESC")
    Page<ChatRoom> findPublicByCommunityIdByCreatedAtWithName(@Param("communityId") Long communityId, @Param("fan") Fan curFan, @Param("name") String name, Pageable pageable);

    @Query("SELECT cr FROM ChatRoom cr LEFT JOIN ChatSession cs ON cr.id = cs.chatRoom.id LEFT JOIN Chat c ON c.chatRoom.id = cr.id WHERE cs.fan.user = :user AND cr.type = 'PUBLIC' GROUP BY cr.id ORDER BY MAX(c.createdAt) DESC NULLS LAST")
    List<ChatRoom> findPublicByUser(@Param("user") User user);

    Optional<ChatRoom> findByCommunityId(Long id);

    @Query("SELECT cr FROM ChatRoom cr LEFT JOIN Fan f ON f.communityId = cr.communityId WHERE f.user = :user AND cr.type = 'OFFICIAL' ORDER BY f.communityOrder ASC")
    List<ChatRoom> findMyOfficial(@Param("user") User user);

    @Query("""
    SELECT c FROM ChatRoom c 
    WHERE c.type = :type 
      AND c.meet.id = :meetId
    """)
    Optional<ChatRoom> findConfirmedChatRoomByMeetId(
            @Param("meetId") Long meetId,
            @Param("type") ChatRoomType type
    );

    @Query("""
    SELECT c FROM ChatRoom c
    WHERE c.type = :type
      AND c.meet.id = :meetId
      AND c.id IN (
          SELECT cs.chatRoom.id FROM ChatSession cs
          WHERE cs.fan = :manager OR cs.fan = :applicant
          GROUP BY cs.chatRoom.id
          HAVING COUNT(cs.chatRoom.id) = 2
    )
    """)
    Optional<ChatRoom> findPrivateChatRoomByParticipantsAndMeet(
            @Param("manager") Fan manager,
            @Param("applicant") Fan applicant,
            @Param("type") ChatRoomType type,
            @Param("meetId") Long meetId
    );

    @Query("SELECT c.id FROM ChatRoom c WHERE c.manager.id = :managerId AND c.type = :type AND c.meet.id = :meetId")
    List<Long> findPrivateChatRoomIdsByManagerAndMeet(@Param("managerId") Long managerId, @Param("type") ChatRoomType type, @Param("meetId") Long meetId);




}