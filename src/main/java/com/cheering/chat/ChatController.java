package com.cheering.chat;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import com.cheering.chat.chatRoom.ChatRoomService;
import com.cheering.fan.Fan;
import com.cheering.meet.MeetService;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatRoomService chatRoomService;
    private final MeetService meetService;

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

    // 확정 질문 메시지 보내기
    @MessageMapping("/chatRooms/{chatRoomId}/join-message")
    public void createJoinMessage(
            @Payload ChatRequest.ChatRequestDTO requestDTO,
            @DestinationVariable String chatRoomId) {

        chatRoomService.sendJoinRequest(requestDTO, Long.parseLong(chatRoomId));
    }

    @MessageMapping("/fans/{fanId}/meets/{meetId}/join-applier")
    public void joinAsApplier(, @DestinationVariable Long fanId, @DestinationVariable Long meetId) {
        meetService.joinAsApplier(meetId, fanId);
    }
}
