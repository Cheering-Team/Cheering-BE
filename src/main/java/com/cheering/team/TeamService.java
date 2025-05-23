package com.cheering.team;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.chat.chatRoom.ChatRoomRepository;
import com.cheering.chat.chatRoom.ChatRoomType;
import com.cheering.community.CommunityResponse;
import com.cheering.fan.CommunityType;
import com.cheering.fan.Fan;
import com.cheering.fan.FanResponse;
import com.cheering.player.PlayerRepository;
import com.cheering.fan.FanRepository;
import com.cheering.team.league.League;
import com.cheering.team.league.LeagueRepository;
import com.cheering.team.relation.TeamPlayer;
import com.cheering.team.relation.TeamPlayerRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final TeamPlayerRepository teamPlayerRepository;
    private final LeagueRepository leagueRepository;
    private final ChatRoomRepository chatRoomRepository;

    public List<TeamResponse.TeamDTO> getTeams(Long leagueId) {
        List<Team> teams = teamRepository.findByLeagueIdOrderByKoreanName(leagueId);

        return teams.stream().map((TeamResponse.TeamDTO::new)).toList();
    }

    public List<TeamResponse.TeamDTO> getTeamsByPlayer(Long playerId) {
        List<Team> teams = teamPlayerRepository.findByPlayerId(playerId);

        return teams.stream().map((TeamResponse.TeamDTO::new)).toList();
    }

    public List<TeamResponse.TeamWithLeagueDTO> searchTeams(String name) {
        List<Team> teams;

        if(name.isEmpty()) {
            teams = teamRepository.findAllOrderByFan();
        } else {
            teams = teamRepository.findByName(name);
        }

        return teams.stream().map((TeamResponse.TeamWithLeagueDTO::new)).toList();
    }

    public void registerTeam(Long leagueId, TeamRequest.RegisterTeamDTO requestDTO) {
        League league = leagueRepository.findById(leagueId).orElseThrow(()->new CustomException(ExceptionCode.LEAGUE_NOT_FOUND));

        Team team = Team.builder()
                .koreanName(requestDTO.koreanName())
                .shortName(requestDTO.shortName())
                .image(requestDTO.image())
                .radarId(requestDTO.radarId())
                .location(requestDTO.location())
                .league(league)
                .build();

        teamRepository.save(team);

        ChatRoom newChatRoom = ChatRoom.builder()
                .type(ChatRoomType.OFFICIAL)
                .communityType(CommunityType.TEAM)
                .name(team.getKoreanName())
                .image(team.getImage())
                .description(team.getKoreanName() + " 팬들끼리 응원해요!")
                .communityId(team.getId())
                .build();

        chatRoomRepository.save(newChatRoom);
    }

    public List<TeamResponse.TeamWithLeagueDTO> getPopularTeams() {
        LocalDateTime lastWeek = LocalDateTime.now().minusWeeks(1);
        Pageable topTen = PageRequest.of(0, 10);
        List<Team> teams = teamRepository.findTop10TeamsByRecentFanCount(lastWeek, topTen);

        return teams.stream().map(TeamResponse.TeamWithLeagueDTO::new).toList();
    }
}
