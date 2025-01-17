package com.cheering.report.memberReport;

import com.cheering.fan.Fan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberReportRepository extends JpaRepository<MemberReport, Long> {

    Optional<MemberReport> findByReportedFanAndWriter(Fan reportedFan, Fan writer);

}
