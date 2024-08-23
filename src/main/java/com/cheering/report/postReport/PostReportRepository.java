package com.cheering.report.postReport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    @Query("select pr from PostReport pr where pr.post.id=:postId and pr.playerUser.id=:playerUserId")
    Optional<PostReport> findByPostIdAndPlayerUserId (@Param("postId") Long postId, @Param("playerUserId") Long playerUserId);

    @Query("SELECT COUNT(pr) FROM PostReport pr WHERE pr.post.id = :postId")
    Long countByPostId(@Param("postId") Long postId);
}
