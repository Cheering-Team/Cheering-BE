package com.cheering.chat.ChatRoom;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.player.Player;
import com.cheering.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final PlayerRepository playerRepository;

    public List<ChatRoomResponse.ChatRoomDTO> getChatRooms(Long playerId) {
        Player player = playerRepository.findById(playerId).orElseThrow(() -> new CustomException(ExceptionCode.PLAYER_NOT_FOUND));

        List<ChatRoom> chatRooms = chatRoomRepository.findByPlayer(player);

        return chatRooms.stream().map(chatRoom -> new ChatRoomResponse.ChatRoomDTO(chatRoom, player)).toList();
    }
}
