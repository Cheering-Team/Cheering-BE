package com.cheering.chat;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.chat.ChatRoom.ChatRoom;
import com.cheering.chat.ChatRoom.ChatRoomRepository;
import com.cheering.chat.ChatRoom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatRoomService chatRoomService;

    @EventListener(SessionConnectEvent.class)
    public void onConnect(SessionConnectEvent event) {
        String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();

        Map<String, List<String>> nativeHeaders = (Map<String, List<String>>) event.getMessage().getHeaders().get("nativeHeaders");

        String userId = nativeHeaders.get("User").get(0);
        String chatRoomId = nativeHeaders.get("chatRoomId").get(0);

        chatRoomService.addUserToRoom(Long.valueOf(chatRoomId), sessionId, Long.valueOf(userId));
    }

    @MessageMapping("/chat")
    public void sendMessage(ChatRequest.ChatRequestDTO chatDTO, SimpMessageHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        chatRoomService.sendMessage(chatDTO, sessionId);
    }

    @MessageMapping("/disconnect")
    public void disconnect(ChatRequest.ChatDisconnectDTO chatDisconnectDTO, SimpMessageHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        Long chatRoomId = chatDisconnectDTO.chatRoomId();
        chatRoomService.removeUserFromRoom(chatRoomId, sessionId);
    }
}
