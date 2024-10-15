package com.cheering.player;

import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserResponse;
import com.cheering.team.Team;
import com.cheering.team.TeamResponse;
import com.cheering.team.league.League;
import com.cheering.team.sport.Sport;

import java.util.List;
import java.util.Optional;

public class PlayerResponse {
    public record PlayerDTO(Long id, String koreanName, String englishName, String image, String backgroundImage, Long fanCount, PlayerUserResponse.PlayerUserDTO user, Long officialChatRoomId, PlayerUserResponse.PlayerUserDTO owner) {
        public PlayerDTO(Player player, Long fanCount) {
            this(player.getId(), player.getKoreanName(), player.getEnglishName(),player.getImage(), player.getBackgroundImage(), fanCount, null, null, Optional.ofNullable(player.getOwner())
                    .map(PlayerUserResponse.PlayerUserDTO::new)
                    .orElse(null));
        }
        public PlayerDTO(Player player, Long fanCount, PlayerUserResponse.PlayerUserDTO playerUserDTO) {
            this(player.getId(), player.getKoreanName(), player.getEnglishName(),player.getImage(), player.getBackgroundImage(), fanCount, playerUserDTO, null, Optional.ofNullable(player.getOwner())
                    .map(PlayerUserResponse.PlayerUserDTO::new)
                    .orElse(null));
        }

        public PlayerDTO(Player player, PlayerUserResponse.PlayerUserDTO playerUserDTO, Long officialChatRoomId) {
            this(player.getId(), player.getKoreanName(), player.getEnglishName(),player.getImage(), player.getBackgroundImage(), null, playerUserDTO, officialChatRoomId, Optional.ofNullable(player.getOwner())
                    .map(PlayerUserResponse.PlayerUserDTO::new)
                    .orElse(null));
        }

        public PlayerDTO(Player player) {
            this(player.getId(), player.getKoreanName(), player.getEnglishName(),player.getImage(), player.getBackgroundImage(), null, null, null, null);
        }
    }

    public record PlayersOfTeamDTO(String sportName, String leagueName, TeamResponse.TeamDTO team, List<PlayerDTO> players) {
        public PlayersOfTeamDTO(Sport sport, League league, TeamResponse.TeamDTO team, List<PlayerDTO> players) {
            this(sport.getName(), league.getName(), team, players);
        }
    }

    public record PlayerAndTeamsDTO(Long id, String koreanName, String englishName, String image, String backgroundImage, Long fanCount, PlayerUserResponse.PlayerUserDTO user, List<TeamResponse.TeamDTO> teams, String sportName, String leagueName, Boolean isOwner, PlayerUserResponse.PlayerUserDTO owner) {

        public PlayerAndTeamsDTO(Player player, Long fanCount, PlayerUserResponse.PlayerUserDTO playerUserDTO, List<TeamResponse.TeamDTO> teams, String sportName, String leagueName, Boolean isOwner) {
            this(player.getId(), player.getKoreanName(), player.getEnglishName(), player.getImage(), player.getBackgroundImage(), fanCount, playerUserDTO, teams, sportName, leagueName, isOwner, Optional.ofNullable(player.getOwner())
                    .map(PlayerUserResponse.PlayerUserDTO::new)
                    .orElse(null));
        }
    }
}
