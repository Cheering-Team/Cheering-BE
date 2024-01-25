package com.cheering.community.domain.repository;

import com.cheering.community.domain.PlayerCommunity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlayerCommunityRepository extends JpaRepository<PlayerCommunity, Long> {

    @Query("select c from PlayerCommunity c where c.name = :name")
    List<PlayerCommunity> findByName(@Param("name") String name);

    List<PlayerCommunity> findByNameContainingIgnoreCase(@Param("name") String name);

}
