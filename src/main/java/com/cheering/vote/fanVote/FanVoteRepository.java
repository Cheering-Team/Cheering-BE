package com.cheering.vote.fanVote;

import com.cheering.fan.Fan;
import com.cheering.vote.Vote;
import com.cheering.vote.voteOption.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FanVoteRepository extends JpaRepository<FanVote, Long> {
    Optional<FanVote> findByVoteAndFan(Vote vote, Fan curFan);

    long countByVote(Vote vote);

    long countByVoteOption(VoteOption option);

    Optional<FanVote> findByVoteOptionAndFan(VoteOption voteOption, Fan curFan);
}
