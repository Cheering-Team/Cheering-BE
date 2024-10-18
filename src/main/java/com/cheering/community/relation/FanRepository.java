package com.cheering.community.relation;

import com.cheering.community.Community;
import com.cheering.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FanRepository extends JpaRepository<Fan, Long> {

    Optional<Fan> findByCommunityAndUser(Community community, User user);

    @Query("SELECT COUNT(f) FROM Fan f WHERE f.community=:community")
    long countByCommunity(@Param("community") Community community);

    Optional<Fan> findByCommunityAndName(Community community, String name);

    List<Fan> findByUser(User user);
}
