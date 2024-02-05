package com.cheering.domain.community.dto.response;

import com.cheering.domain.community.constant.BooleanType;
import com.cheering.domain.community.domain.Community;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public record CommunityResponse(
        Long id,
        String name,
        URL image,
        Long fanCount,
        BooleanType isJoin
) {
    public static CommunityResponse of(Community community, BooleanType isJoin) {
        return new CommunityResponse(community.getId(), community.getName(), community.getImage(),
                community.getFanCount(), isJoin);
    }

    public static List<CommunityResponse> ofList(List<Community> playerCommunities, List<Long> joinCommunityIds) {

        List<CommunityResponse> result = new ArrayList<>();

        for (Community community : playerCommunities) {
            if (joinCommunityIds.contains(community.getId())) {
                CommunityResponse communityResponse = new CommunityResponse(community.getId(), community.getName(),
                        community.getImage(),
                        community.getFanCount(),
                        BooleanType.TRUE);

                result.add(communityResponse);
            } else {
                CommunityResponse communityResponse = new CommunityResponse(community.getId(), community.getName(),
                        community.getImage(),
                        community.getFanCount(),
                        BooleanType.FALSE);

                result.add(communityResponse);
            }
        }
        return result;
    }
}
