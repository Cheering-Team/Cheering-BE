package com.cheering.user;

import com.cheering.player.Player;
import com.cheering.player.PlayerResponse;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserResponse;

import java.time.LocalDateTime;

public class UserResponse {
    public record UserDTO(Long id, String phone, String nickname, String role, PlayerResponse.PlayerDTO player) {
        public UserDTO(User user) {
            this(user.getId(), user.getPhone(), user.getNickname(), user.getRole().toString(), null);
        }

        public UserDTO(User user, Player player, Long fanCount) {
            this(user.getId(), user.getPhone(), user.getNickname(), user.getRole().toString(),  new PlayerResponse.PlayerDTO(player, fanCount, null, null, null, null, null, null));
        }

        public UserDTO(User user, Player player, PlayerUser playerUser, Long fanCount) {
            this(user.getId(), user.getPhone(), user.getNickname(), user.getRole().toString(), new PlayerResponse.PlayerDTO(player, fanCount, playerUser, null, null, null, null, null));
        }
    }

    public record UserWithCreatedAtDTO(Long id, String phone, String nickname, LocalDateTime createdAt) {
        public UserWithCreatedAtDTO(User user) {
            this(user.getId(), user.getPhone(), user.getNickname(), user.getCreatedAt());
        }
    }

    public record TokenDTO(String accessToken, String refreshToken) { }
}
