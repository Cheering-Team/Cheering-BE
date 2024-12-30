package com.cheering.meetfan;

import com.cheering.meet.Meet;
import com.cheering.meet.MeetFan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MeetFanRepository extends JpaRepository<com.cheering.meet.MeetFan, Long> {

    @Query("SELECT COUNT(mf) > 0 " +
            "FROM MeetFan mf " +
            "WHERE mf.meet.match.id = :matchId AND mf.fan.id = :fanId AND mf.role = com.cheering.meetfan.MeetFanRole.MANAGER")
    boolean existsByMatchIdAndFanIdAsManager(@Param("matchId") Long matchId, @Param("fanId") Long fanId);


    @Query("SELECT COUNT(mf) FROM MeetFan mf WHERE mf.meet = :meet")
    int countByMeet(@Param("meet") Meet meet);

    Optional<MeetFan> findByMeetAndRole(Meet meet, MeetFanRole role);

    @Query("SELECT mf FROM MeetFan mf " +
            "WHERE mf.meet.id = :meetId AND mf.role = :role")
    Optional<MeetFan> findByMeetIdAndRole(@Param("meetId") Long meetId, @Param("role") MeetFanRole role);

    @Modifying
    @Query("DELETE FROM MeetFan mf WHERE mf.meet = :meet")
    void deleteByMeet(@Param("meet") Meet meet);
}
