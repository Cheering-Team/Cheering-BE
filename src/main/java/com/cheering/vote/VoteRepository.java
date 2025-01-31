package com.cheering.vote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    @Query("SELECT v FROM Vote v LEFT JOIN v.fanVotes fv WHERE v.post.communityId = :communityId AND v.endTime > CURRENT_TIMESTAMP GROUP BY v ORDER BY COUNT(fv) DESC, v.endTime DESC")
    Optional<Vote> findTopVoteByCommunityId(Long communityId);

    @Query("SELECT v FROM Vote v LEFT JOIN v.fanVotes fv " +
            "WHERE v.post.communityId IN :communityIds AND v.endTime > CURRENT_TIMESTAMP " +
            "GROUP BY v " +
            "ORDER BY COUNT(fv) DESC, v.endTime DESC")
    Optional<Vote> findTopVoteByCommunityIds(@Param("communityIds") List<Long> communityIds);


}
