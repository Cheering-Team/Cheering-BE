package com.cheering.chat;

import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class ChatResponse {
    public record ChatDTO (List<String> messages, LocalDateTime createdAt, PlayerUserResponse.PlayerUserDTO sender) {
        public ChatDTO (List<String> messages, LocalDateTime createdAt, PlayerUser sender) {
            this(messages, createdAt, new PlayerUserResponse.PlayerUserDTO(sender));
        }
    }

    public record ChatResponseDTO (String message, LocalDateTime createdAt, PlayerUserResponse.PlayerUserDTO sender) {
        public ChatResponseDTO (String message, LocalDateTime createdAt, PlayerUser sender) {
            this(message, createdAt, new PlayerUserResponse.PlayerUserDTO(sender));
        }
    }

    public record ChatListDTO(List<ChatDTO> chats, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last) {
        public ChatListDTO(Page<?> page, List<ChatDTO> chats) {
            this(chats, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
        }
    }
}
