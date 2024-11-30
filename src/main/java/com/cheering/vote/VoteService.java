package com.cheering.vote;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.fan.CommunityType;
import com.cheering.fan.Fan;
import com.cheering.fan.FanRepository;
import com.cheering.match.Match;
import com.cheering.match.MatchResponse;
import com.cheering.player.Player;
import com.cheering.player.PlayerRepository;
import com.cheering.post.Post;
import com.cheering.post.PostRepository;
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
    private final PostRepository postRepository;
    private final FanRepository fanRepository;
    private final PlayerRepository playerRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final FanVoteRepository fanVoteRepository;

    public VoteResponse.VoteDTO getVote(Long postId, User user) {
        Optional<Post> post = postRepository.findById(postId);

        if(post.isEmpty()) {
            return null;
        }

        Fan curFan = fanRepository.findByCommunityIdAndUser(post.get().getWriter().getCommunityId(), user).orElseThrow(()->new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));
        boolean isTeam = curFan.getType().equals(CommunityType.TEAM);

        Vote vote = post.get().getVote();

        boolean isClosed = vote.getEndTime().isBefore(LocalDateTime.now());

        MatchResponse.VoteMatchDTO matchDTO = null;
        if(vote.getMatch() != null) {
            Match match = vote.getMatch();

            Long curTeamId;
            if(isTeam) {
                curTeamId = curFan.getCommunityId();
            } else {
                Player player = playerRepository.findById(curFan.getCommunityId()).orElseThrow(()-> new CustomException(ExceptionCode.PLAYER_NOT_FOUND));
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

    @Transactional
    public void vote(Long voteOptionId, User user) {
        VoteOption voteOption = voteOptionRepository.findById(voteOptionId).orElseThrow(()-> new CustomException(ExceptionCode.POST_NOT_FOUND));

        if(voteOption.getVote().getEndTime().isBefore(LocalDateTime.now())) {
            throw new CustomException(ExceptionCode.VOTE_IS_CLOSED);
        }

        Fan curFan = fanRepository.findByCommunityIdAndUser(voteOption.getVote().getPost().getWriter().getCommunityId(), user).orElseThrow(()->new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

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
}
