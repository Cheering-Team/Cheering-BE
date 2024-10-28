package com.cheering.chat.chatRoom;

import com.cheering.community.CommunityResponse;
import com.cheering.player.Player;
import com.cheering.fan.Fan;
import com.cheering.fan.FanResponse;
import com.cheering.team.Team;

import java.util.List;

public class ChatRoomResponse {
    public record IdDTO (Long id) { }

    public record ChatRoomDTO (Long id, String name, String image, String description, Integer max, ChatRoomType type, Integer count, FanResponse.FanDTO user, CommunityResponse.CommunityDTO community, FanResponse.FanDTO manager, Boolean isParticipating) {
        public ChatRoomDTO(ChatRoom chatRoom, Integer count, Boolean isParticipating) {
            this(chatRoom.getId(), chatRoom.getName(), chatRoom.getImage(), chatRoom.getDescription(), chatRoom.getMax(), chatRoom.getType(), count, null, null, null, isParticipating);
        }

        public ChatRoomDTO(ChatRoom chatRoom, Integer count, Fan fan, Fan manager, Team team) {
            this(chatRoom.getId(), chatRoom.getName(), chatRoom.getImage(), chatRoom.getDescription(), chatRoom.getMax(), chatRoom.getType(), count, new FanResponse.FanDTO(fan), new CommunityResponse.CommunityDTO(team, null, null), manager != null ? new FanResponse.FanDTO(manager) : null, null);
        }

        public ChatRoomDTO(ChatRoom chatRoom, Integer count, Fan fan, Fan manager, Player player) {
            this(chatRoom.getId(), chatRoom.getName(), chatRoom.getImage(), chatRoom.getDescription(), chatRoom.getMax(), chatRoom.getType(), count, new FanResponse.FanDTO(fan), new CommunityResponse.CommunityDTO(player, null, null), manager != null ? new FanResponse.FanDTO(manager) : null, null);
        }
    }

    public record ChatRoomSectionDTO (String title, List<ChatRoomDTO> data) { }
}
