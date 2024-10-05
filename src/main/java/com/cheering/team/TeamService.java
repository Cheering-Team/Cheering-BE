package com.cheering.team;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.player.Player;
import com.cheering.player.PlayerRepository;
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
    private final PlayerRepository playerRepository;

    public List<TeamResponse.TeamNameDTO> getTeams(Long leagueId) {
        List<Team> teams = teamRepository.findByLeagueId(leagueId);

        return teams.stream().map(TeamResponse.TeamNameDTO::new).toList();
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

        Player teamCommunity = Player.builder()
                .koreanName(requestDTO.firstName() + " " + requestDTO.secondName())
                .englishName(requestDTO.englishName())
                .image(requestDTO.image())
                .backgroundImage(requestDTO.backgroundImage())
                .team(team)
                .build();

        playerRepository.save(teamCommunity);
    }
}
