package com.cheering.report;

import com.cheering.player.relation.PlayerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    @Query("select r from Report r where r.post.id=:postId and r.playerUser.id=:playerUserId")
    Optional<Report> findByPostIdAndPlayerUserId (@Param("postId") Long postId, @Param("playerUserId") Long playerUserId);
}
