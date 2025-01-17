package com.cheering.fan;

public class FanResponse {
    public record FanDTO(Long id, CommunityType type, String name, String image) {
        public FanDTO(Fan fan) {
            this(fan.getId(), fan.getType(), fan.getName(), fan.getImage());
        }
    }

    public record FanProfileDTO(Long id,
                                CommunityType type,
                                String name,
                                String image,
                                String meetName,
                                String meetImage,
                                Boolean isUser) {
        public FanProfileDTO(Fan fan, Boolean isUser) {
            this(fan.getId(), fan.getType(), fan.getName(), fan.getImage(), fan.getMeetName(), fan.getMeetImage(), isUser);
        }

    }
}
