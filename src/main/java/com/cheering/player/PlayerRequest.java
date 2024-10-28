package com.cheering.player;

public class PlayerRequest {
    public record RegisterCommunityDTO(String koreanName, String englishName, String image, String backgroundImage) { }
}
