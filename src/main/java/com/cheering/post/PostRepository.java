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
    @Query("SELECT p FROM Post p WHERE p.writer.communityId = :communityId AND p.isHide = false AND p.id NOT IN (SELECT pr.post.id FROM PostReport pr WHERE pr.writer = :fan AND pr.post.id IS NOT NULL) AND p.writer NOT IN (SELECT b.to FROM Block b WHERE b.from = :fan) ORDER BY p.createdAt DESC")
    Page<Post> findByCommunityId(@Param("communityId") Long communityId, @Param("fan") Fan fan, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.writer.communityId = :communityId AND p.isHide = false AND " +
            "(SELECT COUNT(l) FROM Like l WHERE l.post.id = p.id) >= 3 " +
            "AND p.id NOT IN (SELECT pr.post.id FROM PostReport pr WHERE pr.writer = :fan AND pr.post.id IS NOT NULL)" +
            "AND p.writer NOT IN (SELECT b.to FROM Block b WHERE b.from = :fan)" +
            "ORDER BY p.createdAt DESC")
    Page<Post> findHotPosts(@Param("communityId") Long communityId, @Param("fan") Fan fan, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN PostTag pt ON p.id = pt.post.id JOIN Tag t ON pt.tag.id = t.id WHERE p.writer.communityId = :communityId AND p.isHide = false AND t.name = :tagName AND p.id NOT IN (SELECT pr.post.id FROM PostReport pr WHERE pr.writer = :fan AND pr.post.id IS NOT NULL) AND p.writer NOT IN (SELECT b.to FROM Block b WHERE b.from = :fan) ORDER BY p.createdAt DESC")
    Page<Post> findByCommunityAndTagName(@Param("communityId") Long communityId, @Param("tagName") String tagName, @Param("fan") Fan fan, Pageable pageable);

    // 특정 유저 게시글
    @Query("SELECT p FROM Post p WHERE p.writer = :fan AND p.isHide = false AND p.id NOT IN (SELECT pr.post.id FROM PostReport pr WHERE pr.writer = :curFan AND pr.post.id IS NOT NULL) ORDER BY p.createdAt DESC")
    Page<Post> findByFan(@Param("fan") Fan fan, @Param("curFan") Fan curFan, Pageable pageable);

    // 내 커뮤니티 인기 게시글
    @Query("SELECT p FROM Post p WHERE p.writer.communityId IN :communityIds AND p.isHide = false AND p.id NOT IN (SELECT pr.post.id FROM PostReport pr WHERE pr.writer IN :fans AND pr.post.id IS NOT NULL) AND p.writer NOT IN (SELECT b.to FROM Block b WHERE b.from IN :fans) AND (SELECT COUNT(l) FROM Like l WHERE l.post.id = p.id) >= 1 ORDER BY p.createdAt DESC")
    Page<Post> findMyHotPosts(@Param("communityIds") List<Long> communityIds, @Param("fans") List<Fan> fans, Pageable pageable);
}
