package com.cheering.cheer;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.badword.BadWordService;
import com.cheering.comment.Comment;
import com.cheering.comment.CommentRequest;
import com.cheering.fan.CommunityType;
import com.cheering.fan.Fan;
import com.cheering.fan.FanRepository;
import com.cheering.match.Match;
import com.cheering.match.MatchRepository;
import com.cheering.player.Player;
import com.cheering.player.PlayerRepository;
import com.cheering.team.Team;
import com.cheering.team.TeamRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CheerService {
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final FanRepository fanRepository;
    private final CheerRepository cheerRepository;
    private final BadWordService badWordService;

    public void writeCheer(Long matchId, Long communityId, CommentRequest.WriteCommentDTO requestDTO, User user) {
        Match match = matchRepository.findById(matchId).orElseThrow(()-> new CustomException(ExceptionCode.MATCH_NOT_FOUND));

        Fan fan = fanRepository.findByCommunityIdAndUser(communityId, user).orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        if(badWordService.containsBadWords(requestDTO.content())) {
            throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
        }

        if(fan.getType().equals(CommunityType.TEAM)) {
            Team team = teamRepository.findById(communityId).orElseThrow(() -> new CustomException(ExceptionCode.TEAM_NOT_FOUND));

            Cheer cheer = Cheer.builder()
                    .type(CommunityType.TEAM)
                    .content(requestDTO.content())
                    .communityId(team.getId())
                    .writer(fan)
                    .match(match)
                    .build();

            cheerRepository.save(cheer);
        } else {
            Player player = playerRepository.findById(communityId).orElseThrow(() -> new CustomException(ExceptionCode.PLAYER_NOT_FOUND));

            Cheer cheer = Cheer.builder()
                    .type(CommunityType.PLAYER)
                    .content(requestDTO.content())
                    .communityId(player.getId())
                    .writer(fan)
                    .match(match)
                    .build();

            cheerRepository.save(cheer);
        }
    }

    public CheerResponse.CheerListDTO getCheers(Long matchId, Long communityId, Pageable pageable, User user) {
        Fan curFan = fanRepository.findByCommunityIdAndUser(communityId, user).orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        Match match = matchRepository.findById(matchId).orElseThrow(()-> new CustomException(ExceptionCode.MATCH_NOT_FOUND));

        Page<Cheer> cheerList = cheerRepository.findByMatchAndCommunityId(match, communityId, pageable);

        List<CheerResponse.CheerDTO> cheerDTOList = cheerList.getContent().stream().map((cheer -> new CheerResponse.CheerDTO(cheer, cheer.getWriter().equals(curFan)))).toList();

        return new CheerResponse.CheerListDTO(cheerList, cheerDTOList);
    }

    public void deleteCheer(Long cheerId) {
        Cheer cheer = cheerRepository.findById(cheerId).orElseThrow(()-> new CustomException(ExceptionCode.MATCH_NOT_FOUND));

        cheerRepository.delete(cheer);
    }
}
