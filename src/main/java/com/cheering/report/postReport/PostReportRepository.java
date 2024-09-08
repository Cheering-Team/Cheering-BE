package com.cheering.report.postReport;

import com.cheering.player.relation.PlayerUser;
import com.cheering.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    @Query("select pr from PostReport pr where pr.post.id=:postId and pr.playerUser.id=:playerUserId")
    Optional<PostReport> findByPostIdAndPlayerUserId (@Param("postId") Long postId, @Param("playerUserId") Long playerUserId);

    @Query("SELECT COUNT(pr) FROM PostReport pr WHERE pr.post.id = :postId")
    Long countByPostId(@Param("postId") Long postId);

    List<PostReport> findByPost(Post post);

    void deleteByPlayerUser(PlayerUser playerUser);

    @Query("SELECT pr FROM PostReport pr WHERE pr.post.playerUser=:playerUser")
    List<PostReport> findByPlayerUser(@Param("playerUser") PlayerUser playerUser);
}
