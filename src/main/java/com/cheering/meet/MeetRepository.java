package com.cheering.meet;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cheering.fan.Fan;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MeetRepository extends JpaRepository<Meet, Long> {

    List<Meet> findByCommunityId(Long communityId);

    List<Meet> findByMatchId(Long matchId);

}
