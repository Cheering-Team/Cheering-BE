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
    // 내 모든 선수 게시글
//    @Query("SELECT p FROM Post p WHERE p.writer.communityId IN :communityIds AND p.isHide = false AND p.id NOT IN (SELECT pr.post.id FROM PostReport pr WHERE pr.writer IN :fans AND pr.post.id IS NOT NULL) AND p.writer NOT IN (SELECT b.to FROM Block b WHERE b.from IN :fans) AND p.type = :type ORDER BY p.createdAt DESC")
//    Page<Post> findByCommunityIds(@Param("communityIds") List<Long> communityIds, @Param("type") PostType type, @Param("fans") List<Fan> fans, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.writer.communityId = :communityId AND p.isHide = false AND p.id NOT IN (SELECT pr.post.id FROM PostReport pr WHERE pr.writer = :fan AND pr.post.id IS NOT NULL) AND p.writer NOT IN (SELECT b.to FROM Block b WHERE b.from = :fan) AND p.type = :type ORDER BY p.createdAt DESC")
    Page<Post> findByCommunityId(@Param("communityId") Long communityId, @Param("type") PostType type, @Param("fan") Fan fan, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.writer.communityId = :communityId AND p.isHide = false AND " +
            "(SELECT COUNT(l) FROM Like l WHERE l.post.id = p.id) >= 3 " +
            "AND p.id NOT IN (SELECT pr.post.id FROM PostReport pr WHERE pr.writer = :fan AND pr.post.id IS NOT NULL)" +
            "AND p.writer NOT IN (SELECT b.to FROM Block b WHERE b.from = :fan)" +
            "AND p.type = :type " +
            "ORDER BY p.createdAt DESC")
    Page<Post> findHotPosts(@Param("communityId") Long communityId, @Param("type") PostType type, @Param("fan") Fan fan, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN PostTag pt ON p.id = pt.post.id JOIN Tag t ON pt.tag.id = t.id WHERE p.writer.communityId = :communityId AND p.isHide = false AND t.name = :tagName AND p.id NOT IN (SELECT pr.post.id FROM PostReport pr WHERE pr.writer = :fan AND pr.post.id IS NOT NULL) AND p.writer NOT IN (SELECT b.to FROM Block b WHERE b.from = :fan) AND p.type = :type ORDER BY p.createdAt DESC")
    Page<Post> findByCommunityAndTagName(@Param("communityId") Long communityId, @Param("type") PostType type, @Param("tagName") String tagName, @Param("fan") Fan fan, Pageable pageable);

    // 특정 유저 게시글
    @Query("SELECT p FROM Post p WHERE p.writer = :fan AND p.isHide = false AND p.id NOT IN (SELECT pr.post.id FROM PostReport pr WHERE pr.writer = :curFan AND pr.post.id IS NOT NULL) ORDER BY p.createdAt DESC")
    Page<Post> findByFan(@Param("fan") Fan fan, @Param("curFan") Fan curFan, Pageable pageable);

//    // 모든 데일리 로드
//    @Query("SELECT p FROM Post p WHERE p.writer.community = :community AND p.type = :type ORDER BY p.createdAt DESC")
//    Page<Post> findAllDaily(@Param("community") Player player, @Param("type") PostType postType, Pageable pageable);
//
//    // 특정 날짜의 데일리 로드
//    @Query("SELECT p FROM Post p WHERE p.writer.community = :community AND p.type = :type AND p.createdAt BETWEEN :startOfDay AND :endOfDay")
//    Page<Post> findDaily(@Param("community") Player player, @Param("type") PostType type, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay, Pageable pageable);
//
//    // 특정 월 데일리 유무 로드
//    @Query("SELECT DISTINCT DATE(p.createdAt) FROM Post p WHERE p.writer.community = :community AND p.type = :type")
//    List<String> findDistinctDailyDates(@Param("community") Player player, @Param("type") PostType type);
}
