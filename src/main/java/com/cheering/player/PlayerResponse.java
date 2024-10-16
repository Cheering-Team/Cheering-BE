package com.cheering.player;

import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserResponse;
import com.cheering.team.Team;
import com.cheering.team.TeamResponse;
import com.cheering.team.league.League;
import com.cheering.team.sport.Sport;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PlayerResponse {
    public record PlayerDTO(Long id, String koreanName, String englishName, String image, String backgroundImage, Long fanCount, PlayerUserResponse.PlayerUserDTO user, List<TeamResponse.TeamDTO> teams, String sportName, String leagueName, Boolean isOwner, PlayerUserResponse.PlayerUserDTO owner, Long officialChatRoomId) {

        public PlayerDTO(Player player, Long fanCount, PlayerUser playerUser, List<TeamResponse.TeamDTO> teams, String sportName, String leagueName, Boolean isOwner, Long officialChatRoomId) {
            this(player.getId(), player.getKoreanName(), player.getEnglishName(), player.getImage(), player.getBackgroundImage(), fanCount, Optional.ofNullable(playerUser)
                    .map(PlayerUserResponse.PlayerUserDTO::new)
                    .orElse(null), teams, sportName, leagueName, isOwner, Optional.ofNullable(player.getOwner())
                    .map(PlayerUserResponse.PlayerUserDTO::new)
                    .orElse(null), officialChatRoomId);
        }

        public PlayerDTO(Player player) {
            this(player.getId(), player.getKoreanName(), player.getEnglishName(), player.getImage(), player.getBackgroundImage(), null, null, null, null, null, null, Optional.ofNullable(player.getOwner())
                    .map(PlayerUserResponse.PlayerUserDTO::new)
                    .orElse(null), null);
        }
    }

    public record PlayersOfTeamDTO(String sportName, String leagueName, TeamResponse.TeamDTO team, List<PlayerDTO> players) {
        public PlayersOfTeamDTO(Sport sport, League league, TeamResponse.TeamDTO team, List<PlayerDTO> players) {
            this(sport.getName(), league.getName(), team, players);
        }
    }
}
