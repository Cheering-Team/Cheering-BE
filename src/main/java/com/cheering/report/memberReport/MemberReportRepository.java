package com.cheering.report.memberReport;

import com.cheering.fan.Fan;
import com.cheering.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberReportRepository extends JpaRepository<MemberReport, Long> {

    @Query("SELECT mr FROM MemberReport mr WHERE mr.userId = :userId AND mr.writer = :writer")
    Optional<MemberReport> findByReportedFanAndWriter(@Param("userId") Long userId, @Param("writer") Fan writer);


}
