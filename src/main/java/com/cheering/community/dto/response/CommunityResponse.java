package com.cheering.community.dto.response;

import com.cheering.community.constant.BooleanType;
import com.cheering.community.domain.Community;
import java.util.List;

public record CommunityResponse(
        Long id,
        String name,
        String image,
        Long fanCount,
        BooleanType isJoin
) {
    public static CommunityResponse of(Community community, BooleanType isJoin) {
        return new CommunityResponse(community.getId(), community.getName(), community.getImage(),
                community.getFanCount(), isJoin);
    }

    public static List<CommunityResponse> ofList(List<Community> playerCommunities, BooleanType isJoin) {
        return playerCommunities.stream().map(com ->
                new CommunityResponse(com.getId(), com.getName(), com.getImage(), com.getFanCount(), isJoin)).toList();
    }
}
