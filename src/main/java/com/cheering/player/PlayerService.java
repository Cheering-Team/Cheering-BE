package com.cheering.player;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.team.Team;
import com.cheering.team.TeamRepository;
import com.cheering.team.relation.TeamPlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final TeamPlayerRepository teamPlayerRepository;
    private final TeamRepository teamRepository;

    public List<PlayerResponse.PlayerDTO> getPlayersByTeam(Long teamId) {
        List<Player> players = teamPlayerRepository.findByTeamId(teamId);

        Team team = teamRepository.findById(teamId).orElseThrow(() -> new CustomException(ExceptionCode.TEAM_NOT_FOUND));

        return players.stream().map((player)->new PlayerResponse.PlayerDTO(player, team)).toList();
    }
}
