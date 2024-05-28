package com.cheering.player;

import com.cheering.team.Team;

public class PlayerResponse {
    public record PlayerDTO(Long id, String name, String image, String teamName, Long fanCount) {
        public PlayerDTO(Player player, Team team) {
            this(player.getId(), player.getName(), player.getImage(), team.getName(), player.getFanCount());
        }
    }
}
