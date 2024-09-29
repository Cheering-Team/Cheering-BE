package com.cheering._core.security;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetailsService;

@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
public class FilterChannelInterceptor implements ChannelInterceptor {
    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        assert headerAccessor != null;
        if(headerAccessor.getCommand() == StompCommand.CONNECT) {
            String token = String.valueOf(headerAccessor.getNativeHeader("Authorization").get(0));

            try {
                jwtUtil.isExpired(token);

                CustomUserDetails customUserDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(jwtUtil.getUsername(token));

                Long userId = customUserDetails.getUser().getId();

                headerAccessor.addNativeHeader("User", String.valueOf(userId));
            } catch(ExpiredJwtException e) {
                throw new MessageDeliveryException("EXPIRED");
            }
        }

        return message;
    }
}
