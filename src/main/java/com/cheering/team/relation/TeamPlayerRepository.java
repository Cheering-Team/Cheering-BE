package com.cheering.team.relation;

import com.cheering.player.Player;
import com.cheering.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamPlayerRepository extends JpaRepository<TeamPlayer, Long> {

    @Query("select tp.player from TeamPlayer tp where tp.team=:team")
    List<Player> findByTeam(@Param("team") Team team);

    @Query("select tp.team from TeamPlayer tp where tp.player.id=:playerId")
    List<Team> findByPlayerId(@Param("playerId") Long playerId);
}
