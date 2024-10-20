package com.cheering.chat.chatRoom;

import com.cheering.community.Community;
import com.cheering.community.CommunityResponse;
import com.cheering.community.relation.Fan;
import com.cheering.community.relation.FanResponse;

import java.util.List;

public class ChatRoomResponse {
    public record IdDTO (Long id) { }

    public record ChatRoomDTO (Long id, String name, String image, String description, Integer max, ChatRoomType type, Integer count, FanResponse.FanDTO user, CommunityResponse.CommunityDTO community, FanResponse.FanDTO manager, Boolean isParticipating) {
        public ChatRoomDTO(ChatRoom chatRoom, Integer count, Community community, Boolean isParticipating) {
            this(chatRoom.getId(), chatRoom.getName(), chatRoom.getImage(), chatRoom.getDescription(), chatRoom.getMax(), chatRoom.getType(), count, null, new CommunityResponse.CommunityDTO(community), null, isParticipating);
        }

        public ChatRoomDTO(ChatRoom chatRoom, Integer count, Fan fan, Fan manager) {
            this(chatRoom.getId(), chatRoom.getName(), chatRoom.getImage(), chatRoom.getDescription(), chatRoom.getMax(), chatRoom.getType(), count, new FanResponse.FanDTO(fan), new CommunityResponse.CommunityDTO(fan.getCommunity()), new FanResponse.FanDTO(manager), null);
        }

        public ChatRoomDTO(ChatRoom chatRoom, Integer count, Fan fan) {
            this(chatRoom.getId(), chatRoom.getName(), chatRoom.getImage(), chatRoom.getDescription(), chatRoom.getMax(), chatRoom.getType(), count, new FanResponse.FanDTO(fan), new CommunityResponse.CommunityDTO(fan.getCommunity()), null, null);
        }
    }

    public record ChatRoomSectionDTO (String title, List<ChatRoomDTO> data) { }
}
