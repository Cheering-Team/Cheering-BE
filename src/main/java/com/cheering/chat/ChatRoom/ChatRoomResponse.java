package com.cheering.chat.ChatRoom;

import com.cheering.player.Player;
import com.cheering.player.PlayerResponse;

public class ChatRoomResponse {
    public record ChatRoomDTO (Long id, String name, String image, String description, Integer count) {
        public ChatRoomDTO(ChatRoom chatRoom, Integer count) {
            this(chatRoom.getId(), chatRoom.getName(), chatRoom.getImage(), chatRoom.getDescription(), count);
        }
    }
}
