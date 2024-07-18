package com.cheering.player.relation;

import com.cheering.post.PostResponse;

import java.util.List;

public class PlayerUserResponse {
    public record PlayerUserDTO(Long id, String nickname, String image) {
        public PlayerUserDTO(PlayerUser playerUser) {
            this(playerUser.getId(), playerUser.getNickname(), playerUser.getImage());
        }
    }

    public record ProfileDTO(PlayerUserDTO user, Boolean isUser, Long playerId) { }
}
