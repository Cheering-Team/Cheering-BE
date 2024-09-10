package com.cheering.chat;

public class ChatRequest {
    public record ChatRequestDTO (Long chatRoomId, String message) { }
    public record ChatDisconnectDTO (Long chatRoomId) { }
}
