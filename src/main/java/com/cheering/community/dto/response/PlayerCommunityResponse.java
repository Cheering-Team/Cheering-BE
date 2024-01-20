package com.cheering.community.dto.response;

import com.cheering.community.domain.PlayerCommunity;
import java.util.List;

public record PlayerCommunityResponse(
        Long id,
        String name,
        String image
) {
    public static List<PlayerCommunityResponse> of(List<PlayerCommunity> playerCommunities) {
        return playerCommunities.stream()
                .map(com -> new PlayerCommunityResponse(com.getId(), com.getName(), com.getImage()))
                .toList();
    }
}
