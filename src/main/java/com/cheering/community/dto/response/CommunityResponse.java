package com.cheering.community.dto.response;

import com.cheering.community.domain.Community;
import java.util.List;

public record CommunityResponse(
        Long id,
        String name,
        String image,
        Long fanCount
) {
    public static CommunityResponse of(Community community) {
        return new CommunityResponse(community.getId(), community.getName(), community.getImage(),
                community.getFanCount());
    }

    public static List<CommunityResponse> ofList(List<Community> playerCommunities) {
        return playerCommunities.stream().map(com ->
                new CommunityResponse(com.getId(), com.getName(), com.getImage(), com.getFanCount())).toList();
    }
}
