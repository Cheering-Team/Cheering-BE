package com.cheering.core.user.domain.repository;

import com.cheering.core.user.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}
