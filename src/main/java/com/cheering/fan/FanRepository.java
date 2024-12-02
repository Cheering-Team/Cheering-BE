package com.cheering.fan;

import com.cheering.player.Player;
import com.cheering.team.Team;
import com.cheering.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FanRepository extends JpaRepository<Fan, Long> {

    Optional<Fan> findByCommunityIdAndUser(Long communityId, User user);

    long countByCommunityId(Long communityId);

    Optional<Fan> findByCommunityIdAndName(Long communityId, String name);

    List<Fan> findByUserOrderByCommunityOrderAsc(User user);

    Integer countByUser(User user);

    boolean existsByName(String name);

    Boolean existsByCommunityIdAndUser(Long id, User user);

    @Query("SELECT f FROM Fan f LEFT JOIN Player p ON p.id = f.communityId WHERE p.firstTeam.id = :communityId AND f.user = :user ORDER BY f.communityOrder")
    Page<Fan> findByFirstTeamIdAndUser(@Param("communityId") Long communityId, @Param("user") User user, Pageable pageable);
}
