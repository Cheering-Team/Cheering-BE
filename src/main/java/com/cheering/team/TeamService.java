package com.cheering.team;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.chat.chatRoom.ChatRoomRepository;
import com.cheering.chat.chatRoom.ChatRoomType;
import com.cheering.community.Community;
import com.cheering.community.CommunityRepository;
import com.cheering.community.relation.FanRepository;
import com.cheering.team.league.League;
import com.cheering.team.league.LeagueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final LeagueRepository leagueRepository;
    private final CommunityRepository communityRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final FanRepository fanRepository;

    public List<TeamResponse.TeamNameDTO> getTeams(Long leagueId) {
        List<Team> teams = teamRepository.findByLeagueIdOrderByFirstName(leagueId);

        return teams.stream().map((team -> {
            Community community = team.getCommunity();
            Long fanCount = fanRepository.countByCommunity(community);
            return new TeamResponse.TeamNameDTO(team, fanCount);
        })).toList();
    }

    public TeamResponse.TeamNameDTO getTeamById(Long teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(()-> new CustomException(ExceptionCode.TEAM_NOT_FOUND));

        Long fanCount = fanRepository.countByCommunity(team.getCommunity());

        return new TeamResponse.TeamNameDTO(team, fanCount);
    }

    public void registerTeam(Long leagueId, TeamRequest.RegisterTeamDTO requestDTO) {
        League league = leagueRepository.findById(leagueId).orElseThrow(()->new CustomException(ExceptionCode.LEAGUE_NOT_FOUND));

        Team team = Team.builder()
                .firstName(requestDTO.firstName())
                .secondName(requestDTO.secondName())
                .image(requestDTO.image())
                .league(league)
                .build();

        teamRepository.save(team);

        Community teamCommunity = Community.builder()
                .koreanName(requestDTO.firstName() + " " + requestDTO.secondName())
                .englishName(requestDTO.englishName())
                .image(requestDTO.image())
                .backgroundImage(requestDTO.backgroundImage())
                .team(team)
                .build();

        communityRepository.save(teamCommunity);

        ChatRoom newChatRoom = ChatRoom.builder()
                .community(teamCommunity)
                .name(teamCommunity.getKoreanName())
                .description(teamCommunity.getKoreanName() + " 팬들끼리 응원해요!")
                .type(ChatRoomType.OFFICIAL)
                .image(teamCommunity.getImage())
                .build();

        chatRoomRepository.save(newChatRoom);
    }
}
