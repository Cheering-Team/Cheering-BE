package com.cheering.player;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.team.Team;
import com.cheering.team.TeamRepository;
import com.cheering.team.TeamResponse;
import com.cheering.team.league.League;
import com.cheering.team.league.LeagueRepository;
import com.cheering.team.relation.TeamPlayerRepository;
import com.cheering.team.sport.Sport;
import com.cheering.team.sport.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final TeamPlayerRepository teamPlayerRepository;
    private final TeamRepository teamRepository;

    public PlayerResponse.PlayersByTeamDTO getPlayersByTeam(Long teamId) {
        List<Player> players = teamPlayerRepository.findByTeamId(teamId);


        Team team = teamRepository.findById(teamId).orElseThrow(() -> new CustomException(ExceptionCode.TEAM_NOT_FOUND));
        League league = team.getLeague();
        Sport sport = league.getSport();

        List<PlayerResponse.PlayerDTO> playerDTOS = players.stream().map(PlayerResponse.PlayerDTO::new).toList();
        TeamResponse.TeamDTO teamDTO = new TeamResponse.TeamDTO(team);

        return new PlayerResponse.PlayersByTeamDTO(sport, league, teamDTO, playerDTOS);
    }
}
