package com.cheering.team.relation;

import com.cheering.community.Community;
import com.cheering.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamPlayerRepository extends JpaRepository<TeamPlayer, Long> {

    @Query("select tp.community from TeamPlayer tp where tp.team=:team")
    List<Community> findByTeam(@Param("team") Team team);

    @Query("select tp.team from TeamPlayer tp where tp.community=:community")
    List<Team> findByCommunity(@Param("community") Community community);


}
