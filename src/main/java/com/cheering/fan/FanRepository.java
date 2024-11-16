package com.cheering.fan;

import com.cheering.player.Player;
import com.cheering.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FanRepository extends JpaRepository<Fan, Long> {

    Optional<Fan> findByCommunityIdAndUser(Long communityId, User user);

    long countByCommunityId(Long communityId);

    Optional<Fan> findByCommunityIdAndName(Long communityId, String name);

    List<Fan> findByUserOrderByCommunityOrderAsc(User user);

    Integer countByUser(User user);

    boolean existsByName(String name);
}
