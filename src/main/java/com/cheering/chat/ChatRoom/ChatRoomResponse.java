package com.cheering.chat.ChatRoom;

import com.cheering.player.Player;
import com.cheering.player.PlayerResponse;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserResponse;

public class ChatRoomResponse {
    public record ChatRoomDTO (Long id, String name, String image, String description, Integer count, PlayerUserResponse.PlayerUserDTO playerUser) {
        public ChatRoomDTO(ChatRoom chatRoom, Integer count) {
            this(chatRoom.getId(), chatRoom.getName(), chatRoom.getImage(), chatRoom.getDescription(), count, null);
        }

        public ChatRoomDTO(ChatRoom chatRoom, Integer count, PlayerUser playerUser) {
            this(chatRoom.getId(), chatRoom.getName(), chatRoom.getImage(), chatRoom.getDescription(), count, new PlayerUserResponse.PlayerUserDTO(playerUser));
        }
    }
}
