package com.cheering.meet;

import com.cheering.chat.chatRoom.ChatRoomType;
import com.cheering.meetfan.MeetFanRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cheering.fan.Fan;
import com.cheering.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MeetRepository extends JpaRepository<Meet, Long> {

    @Query("SELECT m FROM Meet m WHERE m.communityId = :communityId")
    List<Meet> findByCommunityId(@Param("communityId") Long communityId);

    @Query("SELECT m FROM Meet m WHERE " +
            "(:keyword IS NULL OR m.title LIKE %:keyword% OR m.place LIKE %:keyword%) " +
            "AND (:type IS NULL OR m.type = :type) " +
            "AND (:genders IS NULL OR m.gender IN :genders) " +
            "AND (:minAge IS NULL OR m.ageMin >= :minAge) " +
            "AND (:maxAge IS NULL OR m.ageMax <= :maxAge) " +
            "AND (:matchId IS NULL OR m.match.id = :matchId) " +
            "AND (:hasTicket IS NULL OR m.hasTicket = :hasTicket)")
    Page<Meet> findByFilters(
            @Param("keyword") String keyword,
            @Param("type") MeetType type,
            @Param("genders") List<MeetGender> genders,
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge,
            @Param("matchId") Long matchId,
            @Param("hasTicket") Boolean hasTicket,
            Pageable pageable
    );


    @Query("SELECT COUNT(mf) > 0 " +
            "FROM MeetFan mf " +
            "WHERE mf.meet.match.id = :matchId AND mf.fan.id = :fanId AND mf.role = 'MANAGER'")
    boolean existsByMatchIdAndFanIdAsManager(@Param("matchId") Long matchId, @Param("fanId") Long fanId);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END " +
            "FROM Meet m " +
            "JOIN m.meetFans mf " +
            "WHERE m.match.id = :matchId AND mf.fan.user = :user")
    boolean existsByMatchAndMeetFansFanUser(@Param("matchId") Long matchId, @Param("user") User user);

    @Query("SELECT mf.meet FROM MeetFan mf JOIN mf.meet m WHERE mf.fan.user = :user AND m.communityId = :communityId ORDER BY m.match.time ASC")
    Page<Meet> findConfirmedMeetsByCommunityAndUser(Long communityId, User user, Pageable pageable);

    @Query("SELECT m FROM ChatRoom cr JOIN cr.meet m JOIN m.match mt WHERE cr.type = :chatRoomType AND cr.communityId = :communityId AND EXISTS (SELECT 1 FROM ChatSession cs " +
            "JOIN cs.fan f WHERE cs.chatRoom = cr AND f.user = :user) ORDER BY mt.time ASC")
    Page<Meet> findPrivateChatRoomMeetsByCommunityAndUser(
            User user,
            Long communityId,
            ChatRoomType chatRoomType,
            Pageable pageable
    );

}
