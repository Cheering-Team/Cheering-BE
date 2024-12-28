package com.cheering.cheer;

import com.cheering.fan.Fan;
import com.cheering.match.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CheerRepository extends JpaRepository<Cheer, Long> {
    @Query("SELECT c FROM Cheer c LEFT JOIN Like l ON c.id = l.targetId AND l.targetType = 'CHEER' WHERE c.match = :match AND c.communityId = :communityId GROUP BY c.id, c.createdAt, c.writer, c.communityId, c.match, c.type, c.content, c.updatedAt ORDER BY CASE WHEN c.writer = :curFan THEN 0 ELSE 1 END, CASE WHEN c.writer = :curFan THEN c.createdAt END DESC, COUNT(l.id) DESC, c.createdAt ASC")
    Page<Cheer> findByMatchAndCommunityId(Match match, Long communityId, Fan curFan, Pageable pageable);
}
