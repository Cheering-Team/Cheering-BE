package com.cheering.post;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.playerUser.player.id = :playerId ORDER BY p.createdAt DESC")
    List<Post> findByPlayerId(@Param("playerId") Long playerId);

    @Query("SELECT p FROM Post p WHERE p.playerUser.player.id = :playerId AND " +
            "(SELECT COUNT(l) FROM Like l WHERE l.post.id = p.id) >= 3 " +
            "ORDER BY p.createdAt DESC")
    List<Post> findHotPosts(@Param("playerId") Long playerId);

    @Query("SELECT p FROM Post p JOIN PostTag pt ON p.id = pt.post.id JOIN Tag t ON pt.tag.id = t.id WHERE p.playerUser.player.id = :playerId AND t.name = :tagName ORDER BY p.createdAt DESC")
    List<Post> findByPlayerIdAndTagName(@Param("playerId") Long playerId, @Param("tagName") String tagName);

}
