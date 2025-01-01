package com.cheering.meet;

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

    //List<Meet> findByMatchId(Long matchId);

    /*
    @Query("SELECT m FROM Meet m WHERE (:type IS NULL OR m.type = :type) AND (:gender IS NULL OR m.gender = :gender) AND (:minAge IS NULL OR m.ageMin >= :minAge) AND (:maxAge IS NULL OR m.ageMax <= :maxAge) AND (:matchId IS NULL OR m.match.id = :matchId) AND (:hasTicket IS NULL OR m.hasTicket = :hasTicket) AND (:location IS NULL OR m.place LIKE :location) ORDER BY m.createdAt DESC")
    Page<Meet> findByFilters(
            @Param("type") MeetType type,
            @Param("gender") MeetGender gender,
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge,
            @Param("matchId") Long matchId,
            @Param("hasTicket") Boolean hasTicket,
            @Param("location") String location,
            Pageable pageable
    );
     */

    @Query("SELECT m FROM Meet m " +
            "WHERE (:type IS NULL OR m.type = :type) " +
            "AND (:gender IS NULL OR m.gender = :gender) " +
            "AND (:minAge IS NULL OR m.ageMin >= :minAge) " +
            "AND (:maxAge IS NULL OR m.ageMax <= :maxAge) " +
            "AND (:matchId IS NULL OR m.match.id = :matchId) " +
            "AND (:hasTicket IS NULL OR m.hasTicket = :hasTicket) " +
            "AND (:location IS NULL OR m.place LIKE %:location%)")
    Page<Meet> findByFilters(
            @Param("type") MeetType type,
            @Param("gender") MeetGender gender,
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge,
            @Param("matchId") Long matchId,
            @Param("hasTicket") Boolean hasTicket,
            @Param("location") String location,
            Pageable pageable
    );


    @Query("SELECT COUNT(mf) > 0 " +
            "FROM MeetFan mf " +
            "WHERE mf.meet.match.id = :matchId AND mf.fan.id = :fanId AND mf.role = 'MANAGER'")
    boolean existsByMatchIdAndFanIdAsManager(@Param("matchId") Long matchId, @Param("fanId") Long fanId);

}
