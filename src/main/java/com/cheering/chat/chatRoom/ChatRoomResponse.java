package com.cheering.chat.chatRoom;

import com.cheering.community.CommunityResponse;
import com.cheering.meet.MeetResponse;
import com.cheering.player.Player;
import com.cheering.fan.Fan;
import com.cheering.fan.FanResponse;
import com.cheering.team.Team;
import com.cheering.user.Gender;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class ChatRoomResponse {
    public record IdDTO (Long id) { }

    public record IdWithConditionDTO(Long chatRoomId, boolean isConditionMatched) { }

    public record ChatRoomDTO (Long id,
                               String name,
                               String image,
                               String description,
                               Integer max,
                               ChatRoomType type,
                               Integer count,
                               FanResponse.FanDTO user,
                               CommunityResponse.CommunityDTO community,
                               FanResponse.FanDTO manager,
                               Boolean isParticipating,
                               String lastMessage,
                               LocalDateTime lastMessageTime,
                               Integer unreadCount,
                               Long meetId
    ) {
        // 목록에서 사용
        public ChatRoomDTO(ChatRoom chatRoom, Integer count, Boolean isParticipating) {
            this(chatRoom.getId(), chatRoom.getName(), chatRoom.getImage(), chatRoom.getDescription(), chatRoom.getMax(), chatRoom.getType(), count, null, null, null, isParticipating, null, null, null, null);
        }
        // 내 채팅방 목록에서 사용
        public ChatRoomDTO(ChatRoom chatRoom, Integer count, Boolean isParticipating, String lastMessage, LocalDateTime lastMessageTime, Integer unreadCount) {
            this(chatRoom.getId(), chatRoom.getName(), chatRoom.getImage(), chatRoom.getDescription(), chatRoom.getMax(), chatRoom.getType(), count, null, null, null, isParticipating, lastMessage, lastMessageTime, unreadCount, null);
        }
        // 채팅방 내부 정보 (팀)
        public ChatRoomDTO(ChatRoom chatRoom, Integer count, Fan fan, Fan manager, Team team) {
                this(chatRoom.getId(), chatRoom.getName(), chatRoom.getImage(), chatRoom.getDescription(), chatRoom.getMax(), chatRoom.getType(), count, new FanResponse.FanDTO(fan), new CommunityResponse.CommunityDTO(team, null, null), manager != null ? new FanResponse.FanDTO(manager) : null, null, null, null, null, chatRoom.getMeet() == null ? null : chatRoom.getMeet().getId());
        }
        // 채팅방 내부 정보 (선수)
        public ChatRoomDTO(ChatRoom chatRoom, Integer count, Fan fan, Fan manager, Player player) {
            this(chatRoom.getId(), chatRoom.getName(), chatRoom.getImage(), chatRoom.getDescription(), chatRoom.getMax(), chatRoom.getType(), count, new FanResponse.FanDTO(fan), new CommunityResponse.CommunityDTO(player, null, null), manager != null ? new FanResponse.FanDTO(manager) : null, null, null, null, null, chatRoom.getMeet() == null ? null : chatRoom.getMeet().getId());
        }
    }

    public record ChatRoomListDTO(List<ChatRoomDTO> chatRooms, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last, boolean hasNext) {
        public ChatRoomListDTO(Page<?> page, List<ChatRoomDTO> chatRooms) {
            this(chatRooms, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast(), page.hasNext());
        }
    }

    public record PrivateChatRoomDTO(Long id,
                                     ChatRoomType type,
                                     String name,
                                     String image,
                                     Integer opponentAge,
                                     Gender opponentGender,
                                     FanResponse.FanDTO user,
                                     Long meetId,
                                     Long communityId,
                                     Boolean isConfirmed
    ) {
        public PrivateChatRoomDTO(ChatRoom chatRoom, String name, String image, Integer opponentAge, Gender opponentGender, Fan fan, Boolean isConfirmed) {
            this(chatRoom.getId(), chatRoom.getType(), name, image, opponentAge, opponentGender, new FanResponse.FanDTO(fan.getId(), fan.getType(), fan.getMeetName(), fan.getMeetImage()), chatRoom.getMeet().getId(), chatRoom.getCommunityId(), isConfirmed);
        }
    }
}
