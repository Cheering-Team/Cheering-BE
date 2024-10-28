package com.cheering.report.postReport;

import com.cheering.fan.Fan;
import com.cheering.post.Post;
import com.cheering.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    @Query("select pr from PostReport pr where pr.post=:post and pr.writer=:writer")
    Optional<PostReport> findByPostAndWriter(@Param("post") Post post, @Param("writer") Fan writer);

    Long countByPost(Post post);

    List<PostReport> findByPost(Post post);

    @Query("SELECT pr FROM PostReport pr WHERE pr.post.writer=:writer")
    List<PostReport> findByWriter(@Param("writer") Fan fan);

    @Query("SELECT pr FROM PostReport pr WHERE pr.post.writer.user=:user")
    List<PostReport> findByUser(@Param("user") User user);
}
