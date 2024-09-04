package com.cheering.chat;

public class ChatRequest {
    public record ChatDTO (Integer roomId, Integer writerID, String message) { }
}
