package com.cheering.community.dto;

import java.util.List;

public record CommunityResponse(
        String teamName,
        List<PlayerCommunityResponse> playerCommunities
) {
}
