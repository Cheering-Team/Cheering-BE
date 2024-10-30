package com.cheering.match;

import com.cheering.team.Team;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    @Query("SELECT m FROM Match m WHERE (m.homeTeam = :team OR m.awayTeam = :team)")
    List<Match> findByHomeTeamOrAwayTeam(@Param("team") Team team);

}
