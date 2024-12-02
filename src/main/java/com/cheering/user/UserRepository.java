package com.cheering.user;

import com.cheering.player.Player;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);
    Optional<User> findByKakaoId(String kakaoId);
    Optional<User> findByNaverId(String naverId);
    Optional<User> findByAppleId(String appleId);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN Fan f ON f.user = u LEFT JOIN Player p ON f.communityId = p.id WHERE f.communityId = :teamId OR p.firstTeam.id = :teamId")
    List<User> findByTeamId(@Param("teamId") Long teamId);
}
