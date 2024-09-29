package com.cheering.chat.chatRoom;

import com.cheering.player.Player;
import com.cheering.player.PlayerResponse;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserResponse;

import java.util.List;

public class ChatRoomResponse {
    public record IdDTO (Long id) { }

    public record ChatRoomDTO (Long id, String name, String image, String description, Integer max, String type, Integer count, PlayerUserResponse.PlayerUserDTO playerUser, PlayerResponse.PlayerDTO player, PlayerUserResponse.PlayerUserDTO creator, Boolean isParticipating) {
        public ChatRoomDTO(ChatRoom chatRoom, Integer count, Player player, Boolean isParticipating) {
            this(chatRoom.getId(), chatRoom.getName(), chatRoom.getImage(), chatRoom.getDescription(), chatRoom.getMax(), chatRoom.getType().toString(), count, null, new PlayerResponse.PlayerDTO(player), null, isParticipating);
        }

        public ChatRoomDTO(ChatRoom chatRoom, Integer count, PlayerUser playerUser, PlayerUser creator) {
            this(chatRoom.getId(), chatRoom.getName(), chatRoom.getImage(), chatRoom.getDescription(), chatRoom.getMax(), chatRoom.getType().toString(), count, new PlayerUserResponse.PlayerUserDTO(playerUser), new PlayerResponse.PlayerDTO(playerUser.getPlayer()), new PlayerUserResponse.PlayerUserDTO(creator), null);
        }

        public ChatRoomDTO(ChatRoom chatRoom, Integer count, PlayerUser playerUser) {
            this(chatRoom.getId(), chatRoom.getName(), chatRoom.getImage(), chatRoom.getDescription(), chatRoom.getMax(), chatRoom.getType().toString(), count, new PlayerUserResponse.PlayerUserDTO(playerUser), new PlayerResponse.PlayerDTO(playerUser.getPlayer()), null, null);
        }
    }

    public record ChatRoomSectionDTO (String title, List<ChatRoomDTO> data) { }
}
