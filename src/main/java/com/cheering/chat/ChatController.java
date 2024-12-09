package com.cheering.chat;

import com.cheering.chat.chatRoom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatRoomService chatRoomService;

    @MessageMapping("/chatRooms/{chatRoomId}/sendMessage")
    public void handleSendMessage(@Payload ChatRequest.ChatRequestDTO requestDTO, @DestinationVariable String chatRoomId) {
        chatRoomService.sendMessage(requestDTO, Long.parseLong(chatRoomId));
    }

    // 채팅방에서 떠날때 (영구적으로)
    @MessageMapping("/chatRooms/leave")
    public void leaveChatRoom(@Payload ChatRequest.ChatDisconnectDTO chatDisconnectDTO,
                              @Header("simpSessionId") String sessionId) {
        chatRoomService.removeUserFromRoom(chatDisconnectDTO.chatRoomId(), sessionId);
    }

    // 채팅방에서 나갈때
    @MessageMapping("/chatRooms/exit")
    public void exitChatRoom(@Payload ChatRequest.ChatDisconnectDTO chatDisconnectDTO,
                              @Header("simpSessionId") String sessionId) {
        chatRoomService.updateExitTime(chatDisconnectDTO.chatRoomId(), sessionId);
    }
}
