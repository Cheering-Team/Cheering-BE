package com.cheering.chat;

public class ChatRequest {
    public record ChatRequestDTO (String message) { }
    public record ChatDisconnectDTO (Long chatRoomId) { }
}
