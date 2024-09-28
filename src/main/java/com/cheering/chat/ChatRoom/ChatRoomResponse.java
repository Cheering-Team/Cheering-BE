package com.cheering.chat.ChatRoom;

import com.cheering.player.Player;
import com.cheering.player.PlayerResponse;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserResponse;

import java.util.List;

public class ChatRoomResponse {
    public record IdDTO (Long id) { }

    public record ChatRoomDTO (Long id, String name, String image, String description, Integer max, String type, Integer count, PlayerUserResponse.PlayerUserDTO playerUser, Long playerId) {
        public ChatRoomDTO(ChatRoom chatRoom, Integer count, Long playerId) {
            this(chatRoom.getId(), chatRoom.getName(), chatRoom.getImage(), chatRoom.getDescription(), chatRoom.getMax(), chatRoom.getType().toString(), count, null, playerId);
        }

        public ChatRoomDTO(ChatRoom chatRoom, Integer count, PlayerUser playerUser) {
            this(chatRoom.getId(), chatRoom.getName(), chatRoom.getImage(), chatRoom.getDescription(), chatRoom.getMax(), chatRoom.getType().toString(), count, new PlayerUserResponse.PlayerUserDTO(playerUser), playerUser.getPlayer().getId());
        }
    }

    public record ChatRoomSectionDTO (String title, List<ChatRoomDTO> data) { }
}
