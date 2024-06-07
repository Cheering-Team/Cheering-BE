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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final TeamPlayerRepository teamPlayerRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public PlayerResponse.PlayersOfTeamDTO getPlayersByTeam(Long teamId) {
        List<Player> players = teamPlayerRepository.findByTeamId(teamId);


        Team team = teamRepository.findById(teamId).orElseThrow(() -> new CustomException(ExceptionCode.TEAM_NOT_FOUND));
        League league = team.getLeague();
        Sport sport = league.getSport();

        List<PlayerResponse.PlayerDTO> playerDTOS = players.stream().map(PlayerResponse.PlayerDTO::new).toList();
        TeamResponse.TeamDTO teamDTO = new TeamResponse.TeamDTO(team);

        return new PlayerResponse.PlayersOfTeamDTO(sport, league, teamDTO, playerDTOS);
    }

    public PlayerResponse.PlayerAndTeamsDTO getPlayerInfo(Long playerId) {
        Player player = playerRepository.findById(playerId).orElseThrow(()-> new CustomException(ExceptionCode.PLAYER_NOT_FOUND));

        List<Team> teams = teamPlayerRepository.findByPlayerId(playerId);

        List<TeamResponse.TeamDTO> teamDTOS = teams.stream().map(TeamResponse.TeamDTO::new).toList();

        return new PlayerResponse.PlayerAndTeamsDTO(player, teamDTOS);
    }
}
