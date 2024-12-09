package com.cheering.chat;

public class ChatRequest {
    public record ChatRequestDTO (String chatRoomType, Long writerId, String writerImage, String writerName, String content) { }
    public record ChatDisconnectDTO (Long chatRoomId) { }
}
