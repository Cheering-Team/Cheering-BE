package com.cheering.vote;

import com.cheering.match.MatchResponse;
import com.cheering.vote.voteOption.VoteOption;

import java.time.LocalDateTime;
import java.util.List;

public class VoteResponse {

    public record VoteDTO (Long id,
                           String title,
                           LocalDateTime endTime,
                           MatchResponse.VoteMatchDTO match,
                           List<VoteOptionDTO> options,
                           Boolean isVoted,
                           long totalCount
    ) {
        public VoteDTO(Vote vote, MatchResponse.VoteMatchDTO match, List<VoteOptionDTO> options, Boolean isVoted, long totalCount) {
            this(vote.getId(), vote.getTitle(), vote.getEndTime(), match, options, isVoted, totalCount);
        }
    }

    public record VoteOptionDTO (Long id, String name, String image, Long communityId, Long percent, Boolean isVoted) {
        public VoteOptionDTO(VoteOption voteOption, Long
                percent, Boolean isVoted) {
            this(voteOption.getId(), voteOption.getName(), voteOption.getImage(), voteOption.getCommunityId(), percent, isVoted);
        }
    }
}
