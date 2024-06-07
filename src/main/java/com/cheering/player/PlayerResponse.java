package com.cheering.player;

import com.cheering.team.Team;
import com.cheering.team.TeamResponse;
import com.cheering.team.league.League;
import com.cheering.team.sport.Sport;

import java.util.List;

public class PlayerResponse {
    public record PlayerDTO(Long id, String koreanName, String englishName, String image, String backgroundImage, Long fanCount) {
        public PlayerDTO(Player player) {
            this(player.getId(), player.getKoreanName(), player.getEnglishName(),player.getImage(), player.getBackgroundImage(), player.getFanCount());
        }
    }

    public record PlayersOfTeamDTO(String sportName, String leagueName, TeamResponse.TeamDTO team, List<PlayerDTO> players) {
        public PlayersOfTeamDTO(Sport sport, League league, TeamResponse.TeamDTO team, List<PlayerDTO> players) {
            this(sport.getName(), league.getName(), team, players);
        }
    }

    public record PlayerAndTeamsDTO(Long id, String koreanName, String englishName, String backgroundImage, Long fanCount, Boolean isJoin,List<TeamResponse.TeamDTO> teams) {
        public PlayerAndTeamsDTO(Player player, Boolean isJoin,List<TeamResponse.TeamDTO> teams) {
            this(player.getId(), player.getKoreanName(), player.getEnglishName(), player.getBackgroundImage(), player.getFanCount(), isJoin,teams);
        }
    }
}
