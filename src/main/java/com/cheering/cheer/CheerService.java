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
import com.cheering.notification.NotificaitonType;
import com.cheering.notification.Notification;
import com.cheering.player.Player;
import com.cheering.player.PlayerRepository;
import com.cheering.post.Like.Like;
import com.cheering.post.Like.LikeRepository;
import com.cheering.post.PostResponse;
import com.cheering.report.block.BlockRepository;
import com.cheering.team.Team;
import com.cheering.team.TeamRepository;
import com.cheering.user.User;
import com.cheering.user.deviceToken.DeviceToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final LikeRepository likeRepository;
    private final BlockRepository blockRepository;

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

    // 응원 목록 가져오기
    @Transactional
    public CheerResponse.CheerListDTO getCheers(Long matchId, Long communityId, Pageable pageable, User user) {
        Fan curFan = fanRepository.findByCommunityIdAndUser(communityId, user).orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        Match match = matchRepository.findById(matchId).orElseThrow(()-> new CustomException(ExceptionCode.MATCH_NOT_FOUND));

        Page<Cheer> cheerList = cheerRepository.findByMatchAndCommunityId(match, communityId, curFan, pageable);

        List<CheerResponse.CheerDTO> cheerDTOList = cheerList.getContent().stream().map((cheer -> {
            Optional<Like> like = likeRepository.findByTargetIdAndTargetTypeAndFan(cheer.getId(), "CHEER", curFan);
            Long likeCount = likeRepository.countByTargetIdAndTargetType(cheer.getId(), "CHEER");

            return new CheerResponse.CheerDTO(cheer, cheer.getWriter().equals(curFan), like.isPresent(), likeCount);
        })).toList();

        return new CheerResponse.CheerListDTO(cheerList, cheerDTOList);
    }

    public void deleteCheer(Long cheerId) {
        Cheer cheer = cheerRepository.findById(cheerId).orElseThrow(()-> new CustomException(ExceptionCode.MATCH_NOT_FOUND));

        cheerRepository.delete(cheer);
    }

    @Transactional
    // 응원 좋아요
    public void likeCheer(Long communityId, Long cheerId, User user) {
        Optional<Cheer> cheer = cheerRepository.findById(cheerId);

        Fan curFan = fanRepository.findByCommunityIdAndUser(communityId, user).orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        if(cheer.isPresent()) {
            Like newLike = Like.builder()
                    .targetId(cheerId)
                    .targetType("CHEER")
                    .fan(curFan)
                    .build();

            likeRepository.save(newLike);
        }
    }

    @Transactional
    // 응원 좋아요 취소
    public void deleteLikeCheer(Long communityId, Long cheerId, User user) {
        Optional<Cheer> cheer = cheerRepository.findById(cheerId);

        Fan curFan = fanRepository.findByCommunityIdAndUser(communityId, user).orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        if(cheer.isPresent()) {
            Optional<Like> like = likeRepository.findByTargetIdAndTargetTypeAndFan(cheerId, "CHEER", curFan);

            like.ifPresent(likeRepository::delete);
        }
    }
}
