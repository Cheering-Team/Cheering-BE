package com.cheering.post;

import java.util.List;

import com.cheering.player.relation.PlayerUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.playerUser.player.id = :playerId AND p.isHide = false AND p.id NOT IN (SELECT pr.post.id FROM PostReport pr WHERE pr.playerUser = :playerUser) ORDER BY p.createdAt DESC")
    Page<Post> findByPlayerId(@Param("playerId") Long playerId, @Param("playerUser") PlayerUser playerUser, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.playerUser.player.id IN :playerIds AND p.isHide = false AND p.id NOT IN (SELECT pr.post.id FROM PostReport pr WHERE pr.playerUser IN :playerUsers) ORDER BY p.createdAt DESC")
    Page<Post> findByPlayerIds(@Param("playerIds") List<Long> playerIds, @Param("playerUsers") List<PlayerUser> playerUsers, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.playerUser.player.id = :playerId AND p.isHide = false AND " +
            "(SELECT COUNT(l) FROM Like l WHERE l.post.id = p.id) >= 3 " +
            "AND p.id NOT IN (SELECT pr.post.id FROM PostReport pr WHERE pr.playerUser = :playerUser)" +
            "ORDER BY p.createdAt DESC")
    Page<Post> findHotPosts(@Param("playerId") Long playerId, @Param("playerUser") PlayerUser playerUser, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN PostTag pt ON p.id = pt.post.id JOIN Tag t ON pt.tag.id = t.id WHERE p.playerUser.player.id = :playerId AND p.isHide = false AND t.name = :tagName AND p.id NOT IN (SELECT pr.post.id FROM PostReport pr WHERE pr.playerUser = :playerUser) ORDER BY p.createdAt DESC")
    Page<Post> findByPlayerIdAndTagName(@Param("playerId") Long playerId, @Param("tagName") String tagName, @Param("playerUser") PlayerUser playerUser, Pageable pageable);

    Page<Post> findByPlayerUser(PlayerUser playerUser,Pageable pageable);
}
