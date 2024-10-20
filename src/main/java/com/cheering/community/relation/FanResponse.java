package com.cheering.community.relation;

import com.cheering.community.CommunityResponse;

public class FanResponse {
    public record FanDTO(Long id, FanType type, String name, String image) {
        public FanDTO(Fan fan) {
            this(fan.getId(), fan.getType(), fan.getName(), fan.getImage());
        }
    }

    public record ProfileDTO(FanDTO user, Boolean isUser, CommunityResponse.CommunityDTO community) { }
}
