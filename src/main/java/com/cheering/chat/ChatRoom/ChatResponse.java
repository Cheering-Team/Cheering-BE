package com.cheering.chat.ChatRoom;

import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserResponse;

import java.time.LocalDateTime;

public class ChatResponse {
    public record ChatResponseDTO (String message, LocalDateTime createdAt, PlayerUserResponse.PlayerUserDTO sender) {
        public ChatResponseDTO (String message, LocalDateTime createdAt, PlayerUser playerUser) {
            this(message, createdAt, new PlayerUserResponse.PlayerUserDTO(playerUser));
        }
    }
}
