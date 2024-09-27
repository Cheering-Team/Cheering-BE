package com.cheering.chat.ChatRoom;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.chat.ChatRequest;
import com.cheering.player.Player;
import com.cheering.player.PlayerRepository;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final PlayerRepository playerRepository;
    private final PlayerUserRepository playerUserRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    final private Map<Long, Map<String, Long>> chatRoomSessions = new ConcurrentHashMap<>();

    public ChatRoomResponse.IdDTO createChatRoom(Long playerId, User user) {
        PlayerUser curUser = playerUserRepository.findByPlayerIdAndUserId(playerId, user.getId()).orElseThrow(()-> new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        return null;
    }

    public List<ChatRoomResponse.ChatRoomDTO> getChatRooms(Long playerId) {
        Player player = playerRepository.findById(playerId).orElseThrow(() -> new CustomException(ExceptionCode.PLAYER_NOT_FOUND));

        List<ChatRoom> chatRooms = chatRoomRepository.findByPlayer(player);


        return chatRooms.stream().map((chatRoom -> {
            int count = 0;
            if(chatRoomSessions.get(chatRoom.getId()) != null) {
                count = chatRoomSessions.get(chatRoom.getId()).size();
            }
            return new ChatRoomResponse.ChatRoomDTO(chatRoom, count, chatRoom.getPlayer().getId());
        } )).toList();
    }

    public List<ChatRoomResponse.ChatRoomDTO> getMyChatRooms(User user) {
        List<PlayerUser> playerUsers = playerUserRepository.findByUserId(user.getId());

        List<Player> players = playerUsers.stream().map((PlayerUser::getPlayer)).toList();

        List<ChatRoom> chatRooms = chatRoomRepository.findByPlayerIn(players).stream().sorted(Comparator.comparing(chatRoom -> chatRoom.getPlayer().getTeam() != null ? 0 : 1)).toList();

        return chatRooms.stream().map((chatRoom -> {
            int count = 0;
            if(chatRoomSessions.get(chatRoom.getId()) != null) {
                count = chatRoomSessions.get(chatRoom.getId()).size();
            }
            return new ChatRoomResponse.ChatRoomDTO(chatRoom, count, chatRoom.getPlayer().getId());
        } )).toList();
    }

    public ChatRoomResponse.ChatRoomDTO getChatRoomById(Long chatRoomId, User user) {
        // 존재하지 않는 채팅방 -> 뒤로가기
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(()-> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));

        PlayerUser curPlayerUser = playerUserRepository.findByPlayerIdAndUserId(chatRoom.getPlayer().getId(), user.getId()).orElseThrow(()->new CustomException(ExceptionCode.CUR_PLAYER_USER_NOT_FOUND));

        int count = 0;
        if(chatRoomSessions.get(chatRoomId) != null) {
            count = chatRoomSessions.get(chatRoomId).size();
        }

        return new ChatRoomResponse.ChatRoomDTO(chatRoom, count, curPlayerUser);
    }

    public void addUserToRoom(Long chatRoomId, String sessionId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));

        PlayerUser playerUser = playerUserRepository.findByPlayerIdAndUserId(chatRoom.getPlayer().getId(), userId).orElseThrow(()->new CustomException(ExceptionCode.CUR_PLAYER_USER_NOT_FOUND));

        Map<String, Long> sessionMap = chatRoomSessions.computeIfAbsent(chatRoomId, k -> new ConcurrentHashMap<>());

        sessionMap.entrySet().removeIf(entry -> entry.getValue().equals(playerUser.getId()));
        sessionMap.put(sessionId, playerUser.getId());

        broadcastUserCount(chatRoomId);
    }

    public void sendMessage(ChatRequest.ChatRequestDTO chatDTO, String sessionId) {
        Long chatRoomId = chatDTO.chatRoomId();

        Long playerUserId = chatRoomSessions.get(chatRoomId).get(sessionId);

        PlayerUser playerUser = playerUserRepository.findById(playerUserId).orElseThrow(()-> new CustomException(ExceptionCode.CUR_PLAYER_USER_NOT_FOUND));

        LocalDateTime createdAt = LocalDateTime.now();

        simpMessagingTemplate.convertAndSend("/sub/chat/" + chatRoomId, new ChatResponse.ChatResponseDTO(chatDTO.message(), createdAt, playerUser));
    }

    public void removeUserFromRoom(Long chatRoomId, String sessionId) {
        Map<String, Long> session = chatRoomSessions.get(chatRoomId);
        if(session != null) {
            session.remove(sessionId);
            if(session.isEmpty()) {
                chatRoomSessions.remove(chatRoomId);
            }
        }

        broadcastUserCount(chatRoomId);
    }

    private void broadcastUserCount(Long chatRoomId) {
        int count = chatRoomSessions.getOrDefault(chatRoomId, new ConcurrentHashMap<>()).size();
        simpMessagingTemplate.convertAndSend("/sub/" + chatRoomId + "/count", count);
    }
}
