package com.cheering.domain.user.repository;

import com.cheering.domain.user.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}
