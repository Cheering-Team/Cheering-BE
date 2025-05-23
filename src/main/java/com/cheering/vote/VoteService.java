package com.cheering.vote;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.fan.Fan;
import com.cheering.fan.FanRepository;
import com.cheering.match.Match;
import com.cheering.match.MatchResponse;
import com.cheering.player.Player;
import com.cheering.player.PlayerRepository;
import com.cheering.team.Team;
import com.cheering.team.TeamRepository;
import com.cheering.user.User;
import com.cheering.vote.fanVote.FanVote;
import com.cheering.vote.fanVote.FanVoteRepository;
import com.cheering.vote.voteOption.VoteOption;
import com.cheering.vote.voteOption.VoteOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final FanRepository fanRepository;
    private final PlayerRepository playerRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final FanVoteRepository fanVoteRepository;
    private final TeamRepository teamRepository;
    private final VoteRepository voteRepository;

    @Transactional
    public void vote(Long voteOptionId, User user) {
        VoteOption voteOption = voteOptionRepository.findById(voteOptionId).orElseThrow(()-> new CustomException(ExceptionCode.POST_NOT_FOUND));

        if(voteOption.getVote().getEndTime().isBefore(LocalDateTime.now())) {
            throw new CustomException(ExceptionCode.VOTE_IS_CLOSED);
        }

        Fan curFan = fanRepository.findByCommunityIdAndUser(voteOption.getVote().getPost().getCommunityId(), user).orElseThrow(()->new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        Optional<FanVote> fanVote = fanVoteRepository.findByVoteOptionAndFan(voteOption, curFan);

        if(fanVote.isPresent()) {
            fanVoteRepository.delete(fanVote.get());
        } else {
            FanVote newFanVote = FanVote.builder()
                    .fan(curFan)
                    .voteOption(voteOption)
                    .build();

            fanVoteRepository.save(newFanVote);
        }
    }

    public VoteResponse.VoteDTO getHotVote(Long communityId, User user) {
        if (communityId != 0) {
            Optional<Vote> vote = voteRepository.findTopVoteByCommunityId(communityId);

            if(vote.isPresent()) {
                Fan curFan = fanRepository.findByCommunityIdAndUser(communityId, user).orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));
                return getVoteInfo(vote.get(), curFan);
            } else {
                return null;
            }
        }
        List<Long> communityIds = fanRepository.findFansByUser(user).stream()
                .map(Fan::getCommunityId)
                .toList();

        Optional<Vote> topVote = voteRepository.findTopVoteByCommunityIds(communityIds);

        if (topVote.isPresent()) {
            Long voteCommunityId = topVote.get().getPost().getCommunityId();
            Fan curFan = fanRepository.findByCommunityIdAndUser(voteCommunityId, user)
                    .orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));
            return getVoteInfo(topVote.get(), curFan);
        } else {
            return null;
        }

    }

    public VoteResponse.VoteDTO getVoteInfo(Vote vote, Fan curFan) {
        boolean isClosed = vote.getEndTime().isBefore(LocalDateTime.now());

        MatchResponse.VoteMatchDTO matchDTO = null;
        if(vote.getMatch() != null) {
            Match match = vote.getMatch();

            Optional<Team> team = teamRepository.findById(curFan.getCommunityId());

            Long curTeamId;

            if(team.isPresent()) {
                curTeamId = vote.getPost().getCommunityId();
            } else {
                Player player = playerRepository.findById(vote.getPost().getCommunityId()).orElseThrow(()-> new CustomException(ExceptionCode.PLAYER_NOT_FOUND));
                curTeamId = player.getFirstTeam().getId();
            }
            matchDTO = new MatchResponse.VoteMatchDTO(match.getId(), curTeamId.equals(match.getHomeTeam().getId()) ? match.getAwayTeam().getImage() : match.getHomeTeam().getImage(), curTeamId.equals(match.getHomeTeam().getId()) ? match.getAwayTeam().getShortName() : match.getHomeTeam().getShortName(), match.getTime());
        }

        Optional<FanVote> votedFanVote = fanVoteRepository.findByVoteAndFan(vote, curFan);
        VoteOption votedOption;
        votedOption = votedFanVote.map(FanVote::getVoteOption).orElse(null);

        long totalCount = fanVoteRepository.countByVote(vote);

        List<VoteResponse.VoteOptionDTO> options;

        if(isClosed) {
            options = vote.getOptions().stream().map(option -> {
                long count = fanVoteRepository.countByVoteOption(option);
                return new VoteResponse.VoteOptionDTO(option, totalCount != 0 ? (count * 100) / totalCount : 0, option.equals(votedOption));
            }).sorted((dto1, dto2)-> Long.compare(dto2.percent(), dto1.percent())).toList();
        } else {
            options = vote.getOptions().stream().map(option -> {
                long count = fanVoteRepository.countByVoteOption(option);
                return new VoteResponse.VoteOptionDTO(option, votedFanVote.isPresent() ? totalCount != 0 ? (count * 100) / totalCount : 0 : null, option.equals(votedOption));
            }).toList();
        }

        return new VoteResponse.VoteDTO(vote, matchDTO, options, votedFanVote.isPresent(), totalCount, isClosed);
    }
}
