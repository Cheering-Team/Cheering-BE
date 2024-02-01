package com.cheering.domain.user.repository;

import com.cheering.domain.user.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {

}
