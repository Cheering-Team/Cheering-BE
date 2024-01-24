package com.cheering.community.dto.response;

import com.cheering.community.domain.PlayerCommunity;
import java.util.List;

public record CommunityResponse(
        Long id,
        String name,
        String image,
        Long fanCount
) {
    public static List<CommunityResponse> of(List<PlayerCommunity> playerCommunities) {
        return playerCommunities.stream()
                .map(com -> new CommunityResponse(com.getId(), com.getName(), com.getImage(), com.getFanCount()))
                .toList();
    }
}
