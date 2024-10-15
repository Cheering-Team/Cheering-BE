package com.cheering.player.relation;

import com.cheering.player.Player;
import com.cheering.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlayerUserRepository extends JpaRepository<PlayerUser, Long> {
    @Query("select pu from PlayerUser pu where pu.player.id=:playerId and pu.user.id=:userId")
    Optional<PlayerUser> findByPlayerIdAndUserId (@Param("playerId") Long playerId, @Param("userId") Long userId);

    @Query("select pu from PlayerUser pu where pu.player.id=:playerId and pu.nickname=:nickname")
    Optional<PlayerUser> findByPlayerIdAndNickname (@Param("playerId") Long playerId, @Param("nickname") String nickname);

    @Query("SELECT COUNT(pu) FROM PlayerUser pu WHERE pu.player.id=:playerId")
    long countByPlayerId(@Param("playerId") Long playerId);

    @Query("SELECT pu FROM PlayerUser pu WHERE pu.user.id=:userId")
    List<PlayerUser> findByUserId(@Param("userId") Long userId);
    Optional<PlayerUser> findByPlayerAndUser(Player player, User user);
}
