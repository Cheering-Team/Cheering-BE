package com.cheering.meetfan;

import com.cheering.meet.Meet;
import com.cheering.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MeetFanRepository extends JpaRepository<com.cheering.meetfan.MeetFan, Long> {

    @Query("SELECT COUNT(mf) FROM MeetFan mf WHERE mf.meet = :meet AND (mf.role = 'MANAGER' OR mf.role = 'MEMBER')")
    int countByMeet(@Param("meet") Meet meet);

    Optional<MeetFan> findByMeetAndRole(Meet meet, MeetFanRole role);

    @Query("SELECT mf FROM MeetFan mf WHERE mf.meet.id = :meetId AND mf.role = :role")
    Optional<MeetFan> findByMeetIdAndRole(@Param("meetId") Long meetId, @Param("role") MeetFanRole role);

    @Modifying
    @Query("DELETE FROM MeetFan mf WHERE mf.meet = :meet")
    void deleteByMeet(@Param("meet") Meet meet);

    @Query("SELECT CASE WHEN COUNT(mf) > 0 THEN true ELSE false END " +
            "FROM MeetFan mf " +
            "WHERE mf.meet = :meet AND mf.fan.user = :user")
    boolean existsByMeetAndFanUser(@Param("meet") Meet meet, @Param("user") User user);

    @Query("SELECT CASE WHEN COUNT(mf) > 0 THEN true ELSE false END " +
            "FROM MeetFan mf " +
            "WHERE mf.meet = :meet AND mf.fan.user = :user AND mf.role = :role")
    boolean existsByMeetAndFanUserAndRole(@Param("meet") Meet meet,
                                          @Param("user") User user,
                                          @Param("role") MeetFanRole role);

    Optional<MeetFan> findByMeetAndFanUser(@Param("meet") Meet meet, @Param("user") User user);

    @Query("SELECT mf FROM MeetFan mf JOIN mf.meet m JOIN m.match mt WHERE mf.fan.user = :user ORDER BY mt.time ASC")
    Page<MeetFan> findByFanUserOrderByMatchTime(User user, Pageable pageable);

    @Query("SELECT mf FROM MeetFan mf WHERE mf.meet = :meet AND (mf.role = 'MANAGER' OR mf.role = 'MEMBER')")
    List<MeetFan> findByMeetAndRoleIsManagerOrMember(@Param("meet") Meet meet);

    @Query("SELECT mf FROM MeetFan mf WHERE mf.meet = :meet AND mf.role = :role")
    Optional<MeetFan> findByMeetAndRoleIsMember(@Param("meet") Meet meet, @Param("role") MeetFanRole role);
}
