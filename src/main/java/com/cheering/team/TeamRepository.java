package com.cheering.team;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByLeagueIdOrderByKoreanName(Long leagueId);

    @Query("SELECT DISTINCT t FROM Team t LEFT JOIN t.aliases alias WHERE REPLACE(t.koreanName, ' ', '') LIKE %:name% OR REPLACE(t.englishName, ' ', '') LIKE %:name% OR alias LIKE %:name%")
    List<Team> findByName(@Param("name") String name);

    Team findByRadarId(String teamRadarId);

    @Query("SELECT t FROM Team t LEFT JOIN Fan f ON t.id = f.communityId GROUP BY t ORDER BY COUNT(f.id) DESC")
    List<Team> findAllOrderByFan();

    @Query(value = "SELECT * FROM team_tb ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Team findRandomTeam();

    @Query("SELECT t FROM Team t LEFT JOIN Fan f ON t.id = f.communityId WHERE f.createdAt >= :lastWeek OR f IS NULL GROUP BY t.id ORDER BY COUNT(f) DESC")
    List<Team> findTop10TeamsByRecentFanCount(@Param("lastWeek") LocalDateTime lastWeek, Pageable topTen);
}
