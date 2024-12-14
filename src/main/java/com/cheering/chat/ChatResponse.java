package com.cheering.chat;

import com.cheering.fan.Fan;
import com.cheering.fan.FanResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class ChatResponse {
    public record ChatResponseDTO (String type, String content, LocalDateTime createdAt, Long writerId, String writerImage, String writerName, String groupKey, Integer count) {
    }
    public record ChatListDTO(List<ChatGroup> chats, Boolean hasNext) { }
}
