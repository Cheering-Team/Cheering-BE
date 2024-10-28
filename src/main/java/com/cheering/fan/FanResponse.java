package com.cheering.fan;

public class FanResponse {
    public record FanDTO(Long id, CommunityType type, String name, String image) {
        public FanDTO(Fan fan) {
            this(fan.getId(), fan.getType(), fan.getName(), fan.getImage());
        }
    }

    public record ProfileDTO(FanDTO fan, Boolean isUser, String communityKoreanName, String communityEnglishName) { }
}
