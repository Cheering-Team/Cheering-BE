package com.cheering.chat.ChatRoom;

import com.cheering.player.Player;
import com.cheering.player.PlayerResponse;

public class ChatRoomResponse {
    public record ChatRoomDTO (Long id, String name, String description, PlayerResponse.PlayerDTO player) {
        public ChatRoomDTO(ChatRoom chatRoom, Player player) {
            this(chatRoom.getId(), chatRoom.getName(), chatRoom.getDescription(), new PlayerResponse.PlayerDTO(player));
        }
    }
}
