package com.cheering.match;

import com.cheering.team.Team;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    @Query("SELECT m FROM Match m WHERE (m.homeTeam = :team OR m.awayTeam = :team) AND m.time BETWEEN :startDateTime AND :endDateTime ORDER BY m.time ASC")
    List<Match> findByHomeTeamOrAwayTeam(@Param("team") Team team, @Param("startDateTime") LocalDateTime startDateTime,
                                         @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT m FROM Match m WHERE (m.homeTeam = :team OR m.awayTeam = :team) AND m.status IN :statuses ORDER BY m.time ASC")
    List<Match> findNextMatch(@Param("team") Team team, @Param("statuses") List<MatchStatus> statuses, Pageable pageable);

    @Query("SELECT m FROM Match m WHERE m.time BETWEEN :now AND :targetTime AND m.isMatchNotified = false")
    List<Match> findMatchesForReminder(@Param("now") LocalDateTime now, @Param("targetTime") LocalDateTime targetTime);

    @Query("SELECT m FROM Match m WHERE m.status <> :matchStatus ORDER BY m.time ASC, m.id ASC")
    Page<Match> findAllUnfinishedMatch(MatchStatus matchStatus, Pageable pageable);

    @Query("SELECT DISTINCT m FROM Match m " +
            "WHERE (m.homeTeam.id IN :communityIds OR m.awayTeam.id IN :communityIds) " +
            "AND m.time BETWEEN :startOfDay AND :endOfDay")
    List<Match> findDistinctMatchesByCommunityIdsAndTimeRange(
            @Param("communityIds") List<Long> communityIds,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);


}
