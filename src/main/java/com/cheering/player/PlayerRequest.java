package com.cheering.player;

public class PlayerRequest {
    public record RegisterPlayerDTO(String koreanName, String englishName, String image, String backgroundImage) { }
}
