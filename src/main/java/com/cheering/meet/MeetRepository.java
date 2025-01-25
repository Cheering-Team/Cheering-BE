package com.cheering.meet;

import com.cheering.chat.ChatType;
import com.cheering.chat.chatRoom.ChatRoomType;
import com.cheering.meetfan.MeetFanRole;
import com.cheering.user.Gender;
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

    @Query("SELECT m FROM Meet m " +
            "WHERE (m.communityId = :communityId)" +
            "AND (:keyword IS NULL OR m.title LIKE %:keyword%) " +
            "AND (:type IS NULL OR m.type = :type) " +
            "AND (:genders IS NULL OR m.gender IN :genders) " +
            "AND (:minAge IS NULL OR m.ageMin >= :minAge) " +
            "AND (:maxAge IS NULL OR m.ageMax <= :maxAge) " +
            "AND (:matchId IS NULL OR m.match.id = :matchId) " +
            "AND (:hasTicket IS NULL OR m.hasTicket = :hasTicket) " +
            "AND m.match.time > CURRENT_TIMESTAMP " +
            "ORDER BY m.createdAt DESC")
    Page<Meet> findByFilters(
            @Param("communityId") Long communityId,
            @Param("keyword") String keyword,
            @Param("type") MeetType type,
            @Param("genders") List<MeetGender> genders,
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge,
            @Param("matchId") Long matchId,
            @Param("hasTicket") Boolean hasTicket,
            Pageable pageable
    );

    @Query("SELECT CASE WHEN COUNT(mf) > 0 THEN true ELSE false END FROM Meet m JOIN m.meetFans mf WHERE m.match.id = :matchId AND mf.fan.user = :user AND (mf.role = 'MANAGER' OR mf.role = 'MEMBER')")
    boolean existsByMatchAndMeetFansFanUser(@Param("matchId") Long matchId, @Param("user") User user);

    @Query("SELECT m FROM Meet m " +
            "WHERE m.communityId = :communityId " +
            "AND m.ageMin <= :userAge AND m.ageMax >= :userAge " +
            "AND (m.gender = :gender OR m.gender = 'ANY') " +
            "AND m.match.time > CURRENT_TIMESTAMP " +
            "AND m.max > (" +
            "    SELECT COUNT(mf) FROM MeetFan mf WHERE mf.meet = m" +
            ") " +
            "AND NOT EXISTS (" +
            "    SELECT mf FROM MeetFan mf WHERE mf.meet = m AND mf.fan.user = :user" +
            ") " +
            "ORDER BY m.createdAt DESC")
    List<Meet> findMeetsByConditions(@Param("communityId") Long communityId,
                                     @Param("userAge") Integer userAge,
                                     @Param("gender") MeetGender gender,
                                     @Param("user") User user,
                                     Pageable pageable);

    @Query("SELECT m FROM Meet m " +
            "WHERE m.communityId IN :communityIds " +
            "AND m.ageMin <= :userAge AND m.ageMax >= :userAge " +
            "AND (m.gender = :gender OR m.gender = 'ANY') " +
            "AND m.match.time > CURRENT_TIMESTAMP " +
            "AND m.max > (" +
            "    SELECT COUNT(mf) FROM MeetFan mf WHERE mf.meet = m" +
            ") " +
            "AND NOT EXISTS (" +
            "    SELECT mf FROM MeetFan mf WHERE mf.meet = m AND mf.fan.user = :user" +
            ") " +
            "AND NOT EXISTS (" +
            "    SELECT mf FROM MeetFan mf WHERE mf.meet = m AND mf.fan.user = :user" +
            ") " +
            "ORDER BY m.createdAt DESC")
    List<Meet> findMeetsByConditionsForMultipleCommunities(
            @Param("communityIds") List<Long> communityIds,
            @Param("userAge") Integer userAge,
            @Param("gender") MeetGender gender,
            @Param("user") User user,
            Pageable pageable
    );

    @Query("SELECT m FROM Meet m WHERE m.communityId = :communityId AND m.match.time > CURRENT_TIMESTAMP " +
            "AND m.max > (" +
            "    SELECT COUNT(mf) FROM MeetFan mf WHERE mf.meet = m" +
            ") ORDER BY m.createdAt DESC")
    List<Meet> findMeetsByConditionsWithoutProfile(@Param("communityId") Long communityId, Pageable pageable);

    @Query("SELECT m FROM Meet m WHERE m.communityId IN :communityIds AND m.match.time > CURRENT_TIMESTAMP " +
            "AND m.max > (" +
            "    SELECT COUNT(mf) FROM MeetFan mf WHERE mf.meet = m" +
            ") " +
            "ORDER BY m.createdAt DESC")
    List<Meet> findMeetsByConditionsWithoutProfileForMultipleCommunities(@Param("communityIds") List<Long> communityIds, Pageable pageable);

    @Query("SELECT m FROM Meet m " +
            "WHERE m.communityId = :communityId " +
            "AND m.match.id = :matchId " +
            "AND m.match.time > CURRENT_TIMESTAMP " +
            "AND m.max > (" +
            "    SELECT COUNT(mf) FROM MeetFan mf WHERE mf.meet = m" +
            ") " +
            "ORDER BY m.createdAt DESC")
    List<Meet> findMeetsByMatch(@Param("communityId") Long communityId,
                                              @Param("matchId") Long matchId,
                                              Pageable pageable);

    @Query("SELECT m FROM Meet m " +
            "WHERE m.communityId = :communityId " +
            "AND m.ageMin <= :userAge AND m.ageMax >= :userAge " +
            "AND (m.gender = :gender OR m.gender = 'ANY') " +
            "AND m.match.id = :matchId " +
            "AND m.match.time > CURRENT_TIMESTAMP " +
            "AND m.max > (" +
            "    SELECT COUNT(mf) FROM MeetFan mf WHERE mf.meet = m" +
            ") " +
            "AND NOT EXISTS (" +
            "    SELECT mf FROM MeetFan mf WHERE mf.meet = m AND mf.fan.user = :user" +
            ") " +
            "ORDER BY m.createdAt DESC")
    List<Meet> findMeetsByConditionsWithMatch(@Param("communityId") Long communityId,
                                     @Param("userAge") Integer userAge,
                                     @Param("gender") MeetGender gender,
                                     @Param("user") User user,
                                     @Param("matchId") Long matchId,
                                     Pageable pageable);


    @Query("SELECT mf.meet FROM MeetFan mf " +
            "WHERE mf.fan.user = :user " +
            "AND mf.role IN :roles " +
            "AND mf.meet.match.time > CURRENT_TIMESTAMP " +
            "AND mf.meet.communityId = :communityId " +
            "ORDER BY mf.meet.match.time ASC")
    List<Meet> findClosestMeetsByUserAndRoles(@Param("communityId") Long CommunityId, @Param("user") User user, @Param("roles") List<MeetFanRole> roles);

    @Query("SELECT mf.meet FROM MeetFan mf JOIN mf.meet m WHERE mf.fan.user =:user AND m.communityId =:communityId AND (mf.role = 'MANAGER' OR mf.role = 'MEMBER')")
    Page<Meet> findConfirmedMeetsByCommunityIdAndRole(@Param("communityId") Long communityId, @Param("user") User user, Pageable pageable);

    @Query("SELECT mf.meet FROM MeetFan mf JOIN mf.meet m WHERE mf.fan.user = :user AND m.communityId = :communityId " +
            "AND m.match.time < CURRENT_TIMESTAMP " +
            "ORDER BY m.match.time DESC")
    Page<Meet> findPastMeetsByCommunityAndUser(@Param("communityId") Long communityId,
                                               @Param("user") User user,
                                               Pageable pageable);

    @Query("SELECT mf.meet FROM MeetFan mf JOIN mf.meet m WHERE mf.fan.user = :user AND m.communityId = :communityId " +
            "AND m.match.time > CURRENT_TIMESTAMP AND mf.role IN (:roles) ORDER BY m.match.time ASC")
    Page<Meet> findFutureMeetsByCommunityAndUser(@Param("communityId") Long communityId,
                                                          @Param("user") User user,
                                                          @Param("roles") List<MeetFanRole> roles,
                                                          Pageable pageable);

    @Query("SELECT m FROM Meet m " +
            "JOIN m.meetFans mf " +
            "WHERE m.match.id = :matchId " +
            "AND mf.fan.user = :user " +
            "AND mf.role IN :roles")
    Meet findByMatchAndUserWithRoles(@Param("matchId") Long matchId,
                                     @Param("user") User user,
                                     @Param("roles") List<MeetFanRole> roles);

    @Query("SELECT m FROM Meet m " +
            "JOIN m.meetFans mf " +
            "WHERE m.match.id = :matchId " +
            "AND mf.fan.user = :user " +
            "AND mf.role = :role")
    List<Meet> findApplierMeetsByMatchAndUser(@Param("matchId") Long matchId,
                                              @Param("user") User user,
                                              @Param("role") MeetFanRole role);

    @Query("SELECT mf.meet.id FROM MeetFan mf WHERE mf.fan.user = :user")
    List<Long> findMeetIdsByUser(@Param("user") User user);





}
