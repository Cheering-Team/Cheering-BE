package com.cheering.chat;

import com.cheering.fan.Fan;
import com.cheering.fan.FanResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class ChatResponse {
    public record ChatDTO (List<String> messages, LocalDateTime createdAt, FanResponse.FanDTO sender) {
        public ChatDTO (List<String> messages, LocalDateTime createdAt, Fan sender) {
            this(messages, createdAt, new FanResponse.FanDTO(sender));
        }
    }

    public record ChatResponseDTO (String message, LocalDateTime createdAt, FanResponse.FanDTO sender) {
        public ChatResponseDTO (String message, LocalDateTime createdAt, Fan sender) {
            this(message, createdAt, new FanResponse.FanDTO(sender));
        }
    }

    public record ChatListDTO(List<ChatDTO> chats, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last) {
        public ChatListDTO(Page<?> page, List<ChatDTO> chats) {
            this(chats, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
        }
    }
}
