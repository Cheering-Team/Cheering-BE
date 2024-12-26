package com.cheering.post;

import java.time.LocalDateTime;
import java.util.List;

import com.cheering.player.Player;
import com.cheering.fan.Fan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE p.communityId = :communityId AND p.isHide = false AND p.id NOT IN (SELECT pr.post.id FROM PostReport pr WHERE pr.writer = :fan AND pr.post.id IS NOT NULL) AND p.writer NOT IN (SELECT b.to FROM Block b WHERE b.from = :fan) ORDER BY p.createdAt DESC")
    Page<Post> findByCommunityId(@Param("communityId") Long communityId, @Param("fan") Fan fan, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.communityId = :communityId AND p.isHide = false AND " +
            "(SELECT COUNT(l) FROM Like l WHERE l.targetId = p.id AND l.targetType = 'POST') >= 3 " +
            "AND p.id NOT IN (SELECT pr.post.id FROM PostReport pr WHERE pr.writer = :fan AND pr.post.id IS NOT NULL)" +
            "AND p.writer NOT IN (SELECT b.to FROM Block b WHERE b.from = :fan)" +
            "ORDER BY p.createdAt DESC")
    Page<Post> findHotPosts(@Param("communityId") Long communityId, @Param("fan") Fan fan, Pageable pageable);

    @Query("SELECT v.post FROM Vote v  WHERE v.post.communityId = :communityId AND v.post.isHide = false AND v.post.id NOT IN (SELECT pr.post.id FROM PostReport pr WHERE pr.writer = :fan AND pr.post.id IS NOT NULL) AND v.post.writer NOT IN (SELECT b.to FROM Block b WHERE b.from = :fan) ORDER BY v.post.createdAt DESC")
    Page<Post> findHasVotePosts(@Param("communityId") Long communityId, @Param("fan") Fan fan, Pageable pageable);

    // 특정 유저 게시글
    @Query("SELECT p FROM Post p WHERE p.writer = :fan AND p.isHide = false AND p.id NOT IN (SELECT pr.post.id FROM PostReport pr WHERE pr.writer = :curFan AND pr.post.id IS NOT NULL) ORDER BY p.createdAt DESC")
    Page<Post> findByFan(@Param("fan") Fan fan, @Param("curFan") Fan curFan, Pageable pageable);

    // 내 커뮤니티 인기 게시글
    @Query("SELECT p FROM Post p WHERE p.communityId IN :communityIds AND p.isHide = false AND p.id NOT IN (SELECT pr.post.id FROM PostReport pr WHERE pr.writer IN :fans AND pr.post.id IS NOT NULL) AND p.writer NOT IN (SELECT b.to FROM Block b WHERE b.from IN :fans) AND (SELECT COUNT(l) FROM Like l WHERE l.targetId = p.id AND l.targetType = 'POST') >= 3 ORDER BY p.createdAt DESC")
    Page<Post> findMyHotPosts(@Param("communityIds") List<Long> communityIds, @Param("fans") List<Fan> fans, Pageable pageable);

    @Query("SELECT v.post FROM Vote v WHERE v.post.communityId = :communityId AND v.match.id = :matchId AND v.post.isHide = false AND v.post.id NOT IN (SELECT pr.post.id FROM PostReport pr WHERE pr.writer = :fan AND pr.post.id IS NOT NULL) AND v.post.writer NOT IN (SELECT b.to FROM Block b WHERE b.from = :fan) ORDER BY v.post.createdAt DESC")
    Page<Post> findByMatchIdAndCommunityIdOrderByLatest(@Param("communityId") Long communityId, @Param("matchId") Long matchId, @Param("fan") Fan curFan, Pageable pageable);

    @Query("SELECT p FROM Post p LEFT JOIN Vote v ON v.post = p LEFT JOIN FanVote fv ON fv.vote = v WHERE p.communityId = :communityId AND v.match.id = :matchId AND p.isHide = false AND p.id NOT IN (SELECT pr.post.id FROM PostReport pr WHERE pr.writer = :fan AND pr.post.id IS NOT NULL) AND p.writer NOT IN (SELECT b.to FROM Block b WHERE b.from = :fan) GROUP BY p.id ORDER BY COUNT(fv) DESC")
    Page<Post> findByMatchIdAndCommunityIdOrderByVotes(@Param("communityId") Long communityId, @Param("matchId") Long matchId, @Param("fan") Fan curFan, Pageable pageable);
}
