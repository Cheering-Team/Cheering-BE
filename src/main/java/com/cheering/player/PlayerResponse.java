package com.cheering.player;

import com.cheering.player.relation.PlayerUser;
import com.cheering.team.Team;
import com.cheering.team.TeamResponse;
import com.cheering.team.league.League;
import com.cheering.team.sport.Sport;

import java.util.List;

public class PlayerResponse {

    public record PlayerNameDTO(String koreanName, String englishName) {
        public PlayerNameDTO(Player player) {
            this(player.getKoreanName(), player.getEnglishName());
        }
    }
    public record PlayerDTO(Long id, String koreanName, String englishName, String image, String backgroundImage, Long fanCount, PlayerUserDTO user) {
        public PlayerDTO(Player player, Long fanCount) {
            this(player.getId(), player.getKoreanName(), player.getEnglishName(),player.getImage(), player.getBackgroundImage(), fanCount, null);
        }
        public PlayerDTO(Player player, Long fanCount, PlayerUserDTO playerUserDTO) {
            this(player.getId(), player.getKoreanName(), player.getEnglishName(),player.getImage(), player.getBackgroundImage(), fanCount, playerUserDTO);
        }

        public PlayerDTO(Player player, PlayerUserDTO playerUserDTO) {
            this(player.getId(), player.getKoreanName(), player.getEnglishName(),player.getImage(), player.getBackgroundImage(), null, playerUserDTO);
        }
    }

    public record PlayersOfTeamDTO(String sportName, String leagueName, TeamResponse.TeamDTO team, List<PlayerDTO> players) {
        public PlayersOfTeamDTO(Sport sport, League league, TeamResponse.TeamDTO team, List<PlayerDTO> players) {
            this(sport.getName(), league.getName(), team, players);
        }
    }

    public record PlayerUserDTO(Long id, String nickname, String image) {
        public PlayerUserDTO(PlayerUser playerUser) {
            this(playerUser.getId(), playerUser.getNickname(), playerUser.getImage());
        }
    }

    public record PlayerAndTeamsDTO(Long id, String koreanName, String englishName, String image, String backgroundImage, Long fanCount, PlayerUserDTO user, List<TeamResponse.TeamDTO> teams) {

        public PlayerAndTeamsDTO(Player player, Long fanCount, List<TeamResponse.TeamDTO> teams) {
            this(player.getId(), player.getKoreanName(), player.getEnglishName(), player.getImage(), player.getBackgroundImage(), fanCount, null, teams);
        }
        public PlayerAndTeamsDTO(Player player, Long fanCount, PlayerUserDTO playerUserDTO, List<TeamResponse.TeamDTO> teams) {
            this(player.getId(), player.getKoreanName(), player.getEnglishName(), player.getImage(), player.getBackgroundImage(), fanCount, playerUserDTO, teams);
        }
    }
}
