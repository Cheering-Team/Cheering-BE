package com.cheering.player;

import com.cheering.team.Team;
import com.cheering.team.TeamResponse;
import com.cheering.team.league.League;
import com.cheering.team.sport.Sport;

import java.util.List;

public class PlayerResponse {
    public record PlayerDTO(Long id, String name, String image, Long fanCount) {
        public PlayerDTO(Player player) {
            this(player.getId(), player.getName(), player.getImage(), player.getFanCount());
        }
    }

    public record PlayersByTeamDTO(String sportName, String leagueName, TeamResponse.TeamDTO team, List<PlayerDTO> players) {
        public PlayersByTeamDTO(Sport sport, League league, TeamResponse.TeamDTO team, List<PlayerDTO> players) {
            this(sport.getName(), league.getName(), team, players);
        }
    }
}
