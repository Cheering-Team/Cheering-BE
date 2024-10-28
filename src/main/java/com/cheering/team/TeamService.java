package com.cheering.team;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.chat.chatRoom.ChatRoomRepository;
import com.cheering.fan.Fan;
import com.cheering.fan.FanResponse;
import com.cheering.player.PlayerRepository;
import com.cheering.fan.FanRepository;
import com.cheering.team.league.LeagueRepository;
import com.cheering.team.relation.TeamPlayer;
import com.cheering.team.relation.TeamPlayerRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final TeamPlayerRepository teamPlayerRepository;
    private final LeagueRepository leagueRepository;
    private final PlayerRepository playerRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final FanRepository fanRepository;

    public List<TeamResponse.TeamDTO> getTeams(Long leagueId) {
        List<Team> teams = teamRepository.findByLeagueIdOrderByKoreanName(leagueId);

        return teams.stream().map((TeamResponse.TeamDTO::new)).toList();
    }

    public List<TeamResponse.TeamDTO> getTeamsByPlayer(Long playerId) {
        List<Team> teams = teamPlayerRepository.findByPlayerId(playerId);

        return teams.stream().map((TeamResponse.TeamDTO::new)).toList();
    }

//    public void registerTeam(Long leagueId, TeamRequest.RegisterTeamDTO requestDTO) {
//        League league = leagueRepository.findById(leagueId).orElseThrow(()->new CustomException(ExceptionCode.LEAGUE_NOT_FOUND));
//
//        Team team = Team.builder()
//                .firstName(requestDTO.firstName())
//                .secondName(requestDTO.secondName())
//                .image(requestDTO.image())
//                .league(league)
//                .build();
//
//        teamRepository.save(team);
//
//        Player teamPlayer = Player.builder()
//                .koreanName(requestDTO.firstName() + " " + requestDTO.secondName())
//                .englishName(requestDTO.englishName())
//                .image(requestDTO.image())
//                .backgroundImage(requestDTO.backgroundImage())
//                .team(team)
//                .build();
//
//        playerRepository.save(teamPlayer);
//
//        ChatRoom newChatRoom = ChatRoom.builder()
//                .community(teamPlayer)
//                .name(teamPlayer.getKoreanName())
//                .description(teamPlayer.getKoreanName() + " 팬들끼리 응원해요!")
//                .type(ChatRoomType.OFFICIAL)
//                .image(teamPlayer.getImage())
//                .build();
//
//        chatRoomRepository.save(newChatRoom);
//    }
}
