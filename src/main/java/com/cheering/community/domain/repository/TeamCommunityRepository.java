package com.cheering.community.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface TeamCommunityRepository extends JpaRepository<TeamCommunity, Long> {

    TeamCommunity findByNameContainingIgnoreCase(@Param("name") String name);

}
