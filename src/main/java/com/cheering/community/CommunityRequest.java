package com.cheering.community;

public class CommunityRequest {
    public record RegisterCommunityDTO(String koreanName, String englishName, String image, String backgroundImage) { }
}
