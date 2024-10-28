package com.cheering.player;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.chat.chatRoom.ChatRoomRepository;
import com.cheering.chat.chatRoom.ChatRoomType;
import com.cheering.community.CommunityResponse;
import com.cheering.fan.Fan;
import com.cheering.fan.FanRepository;
import com.cheering.fan.FanResponse;
import com.cheering.fan.CommunityType;
import com.cheering.team.Team;
import com.cheering.team.TeamRepository;
import com.cheering.team.TeamResponse;
import com.cheering.team.relation.TeamPlayer;
import com.cheering.team.relation.TeamPlayerRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final TeamPlayerRepository teamPlayerRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final FanRepository fanRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 내가 가입한 커뮤니티 조회
    public List<CommunityResponse.CommunityDTO> getMyCommunities(User user) {
        List<Fan> fans = fanRepository.findByUser(user).stream().sorted(Comparator.comparing(fan -> fan.getType().equals(CommunityType.TEAM) ? 0 : 1)).toList();

        return fans.stream().map((fan -> {
            if(fan.getType().equals(CommunityType.TEAM)) {
                Team team = teamRepository.findById(fan.getCommunityId()).orElseThrow(()-> new CustomException(ExceptionCode.TEAM_NOT_FOUND));
                return new CommunityResponse.CommunityDTO(team, null, new FanResponse.FanDTO(fan));
            } else {
                Player player = playerRepository.findById(fan.getCommunityId()).orElseThrow(()-> new CustomException(ExceptionCode.PLAYER_NOT_FOUND));
                return new CommunityResponse.CommunityDTO(player, null, new FanResponse.FanDTO(fan));
            }
        })).toList();
    }

    public void registerPlayer(Long teamId, PlayerRequest.RegisterCommunityDTO requestDTO) {
        Team team = teamRepository.findById(teamId).orElseThrow(()->new CustomException(ExceptionCode.TEAM_NOT_FOUND));

        Player player = Player.builder()
                .koreanName(requestDTO.koreanName())
                .englishName(requestDTO.englishName())
                .image(requestDTO.image())
                .backgroundImage(requestDTO.backgroundImage())
                .firstTeam(team)
                .build();

        playerRepository.save(player);

        TeamPlayer teamPlayer = TeamPlayer.builder()
                .player(player)
                .team(team)
                .build();

        teamPlayerRepository.save(teamPlayer);

        ChatRoom chatRoom = ChatRoom.builder()
                .type(ChatRoomType.OFFICIAL)
                .communityType(CommunityType.PLAYER)
                .name(requestDTO.koreanName())
                .image(requestDTO.image())
                .description(player.getKoreanName() + " 팬들끼리 응원해요!")
                .communityId(player.getId())
                .build();

        chatRoomRepository.save(chatRoom);
    }
//
//    public void createTeamCommunities() {
//        List<Team> teams = teamRepository.findAll();
//
//        for(Team team : teams) {
//            Optional<Player> teamCommunity = playerRepository.findByTeam(team);
//            if(teamCommunity.isEmpty()){
//                Player player = Player.builder()
//                        .type(PlayerType.TEAM)
//                        .koreanName(team.getKoreanName() + " " + team.getSecondName())
//                        .image(team.getImage())
//                        .team(team)
//                        .build();
//
//                playerRepository.save(player);
//
//                ChatRoom chatRoom = ChatRoom.builder()
//                        .type(ChatRoomType.OFFICIAL)
//                        .name(player.getKoreanName())
//                        .image(team.getImage())
//                        .description(player.getKoreanName() + " 팬들끼리 응원해요!")
//                        .community(player)
//                        .build();
//
//                chatRoomRepository.save(chatRoom);
//            }
//        }
//    }
}
