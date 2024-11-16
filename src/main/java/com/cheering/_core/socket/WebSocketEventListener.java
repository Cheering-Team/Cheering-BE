package com.cheering._core.socket;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.security.JWTUtil;
import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.chat.chatRoom.ChatRoomRepository;
import com.cheering.chat.session.ChatSession;
import com.cheering.chat.session.ChatSessionRepository;
import com.cheering.fan.Fan;
import com.cheering.fan.FanRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final FanRepository fanRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;


    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);

        if(headerAccessor != null) {
            String sessionId = headerAccessor.getSessionId();
            String token = headerAccessor.getFirstNativeHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);

                try {
                    jwtUtil.isExpired(token);

                    String phone = jwtUtil.getUsername(token);
                    CustomUserDetails customUserDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(phone);

                    String destination = headerAccessor.getDestination();
                    String chatRoomId = extractChatRoomId(destination);

                    ChatRoom chatRoom = chatRoomRepository.findById(Long.parseLong(chatRoomId)).orElseThrow(()-> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));

                    Fan fan = fanRepository.findByCommunityIdAndUser(chatRoom.getCommunityId(), customUserDetails.getUser()).orElseThrow(()-> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

                    Optional<ChatSession> chatSession = chatSessionRepository.findByChatRoomAndFan(chatRoom, fan);

                    if(chatSession.isEmpty()) {
                        ChatSession newChatSession = ChatSession.builder()
                                .sessionId(sessionId)
                                .chatRoom(chatRoom)
                                .fan(fan)
                                .build();
                        chatSessionRepository.save(newChatSession);
                    } else {
                        chatSession.get().setSessionId(sessionId);
                        chatSessionRepository.save(chatSession.get());
                    }
                    Integer count = chatSessionRepository.countByChatRoom(chatRoom);
                    simpMessagingTemplate.convertAndSend("/topic/chatRoom/" + chatRoomId + "/participants", count);
                } catch (ExpiredJwtException e) {
                    throw new MessageDeliveryException("Token expired");
                }
            }
        }
    }

    private String extractChatRoomId(String destination) {
        if (destination != null && destination.startsWith("/topic/chatRoom/")) {
            return destination.substring("/topic/chatRoom/".length());
        }
        throw new IllegalArgumentException("Invalid destination format: " + destination);
    }
}
