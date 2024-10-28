package com.cheering.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByLeagueIdOrderByKoreanName(Long leagueId);

    @Query("SELECT DISTINCT t FROM Team t WHERE REPLACE(t.koreanName, ' ', '') LIKE %:name% OR REPLACE(t.englishName, ' ', '') LIKE %:name%")
    List<Team> findByName(@Param("name") String name);
}
