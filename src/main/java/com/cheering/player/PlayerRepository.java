package com.cheering.player;

import com.cheering.team.Team;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Query("SELECT DISTINCT p FROM Player p LEFT JOIN TeamPlayer tp ON p.id = tp.player.id LEFT JOIN Team t ON tp.team.id = t.id WHERE REPLACE(p.koreanName, ' ', '') LIKE %:name% OR REPLACE(p.englishName, ' ', '') LIKE %:name% OR REPLACE(t.koreanName , ' ', '') LIKE %:name% OR REPLACE(t.englishName , ' ', '') LIKE %:name%")
    List<Player> findByNameOrTeamName(@Param("name") String name);

    @Query("SELECT p FROM Player p LEFT JOIN TeamPlayer tp ON p.id = tp.player.id LEFT JOIN Team t ON tp.team.id = t.id WHERE t.id = :teamId AND REPLACE(p.koreanName, ' ', '') LIKE %:name%")
    List<Player> findByNameAndTeamId(@Param("name") String name, @Param("teamId") Long teamId);

    @Query(value = "SELECT * FROM player_tb ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Player findRandomPlayer();

    @Query("SELECT p FROM Player p LEFT JOIN Fan f ON p.id = f.communityId WHERE f.createdAt >= :lastWeek OR f IS NULL GROUP BY p.id ORDER BY COUNT(f) DESC")
    List<Player> findTop10PlayersByRecentFanCount(@Param("lastWeek") LocalDateTime lastWeek, Pageable topTen);
}
