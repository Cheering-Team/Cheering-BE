package com.cheering.cheer;

import com.cheering.comment.Comment;
import com.cheering.match.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheerRepository extends JpaRepository<Cheer, Long> {
    Page<Cheer> findByMatchAndCommunityId(Match match, Long communityId, Pageable pageable);
}
