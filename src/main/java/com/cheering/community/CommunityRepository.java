package com.cheering.community;

import com.cheering.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    @Query("SELECT DISTINCT c FROM Community c LEFT JOIN TeamPlayer tp ON c.id = tp.community.id LEFT JOIN Team t ON tp.team.id = t.id WHERE REPLACE(c.koreanName, ' ', '') LIKE %:name% OR REPLACE(c.englishName, ' ', '') LIKE %:name% OR REPLACE(CONCAT(t.firstName, t.secondName) , ' ', '') LIKE %:name%")
    List<Community> findByNameOrTeamName(@Param("name") String name);

    Optional<Community> findByTeam(Team team);
}
