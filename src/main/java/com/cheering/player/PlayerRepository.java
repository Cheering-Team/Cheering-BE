package com.cheering.player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Query("SELECT DISTINCT p FROM Player p LEFT JOIN TeamPlayer tp ON p.id = tp.player.id LEFT JOIN Team t ON tp.team.id = t.id WHERE REPLACE(p.koreanName, ' ', '') LIKE %:name% OR REPLACE(p.englishName, ' ', '') LIKE %:name% OR REPLACE(CONCAT(t.firstName, t.secondName) , ' ', '') LIKE %:name%")
    List<Player> findByNameOrTeamName(@Param("name") String name);

    Optional<Player> findByTeamId(Long teamId);
}
