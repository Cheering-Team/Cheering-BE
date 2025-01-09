package com.cheering.matchRestriction;

import com.cheering.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRestrictionRepository extends JpaRepository<MatchRestriction, Long> {
    boolean existsByMatchIdAndUser(Long matchId, User user);
}
