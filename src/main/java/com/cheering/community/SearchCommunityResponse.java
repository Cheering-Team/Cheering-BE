package com.cheering.community;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public record SearchCommunityResponse(
        Long id,
        String name,
        URL image,
        Long fanCount,
        BooleanType isJoin
) {
    public static SearchCommunityResponse of(Community community, BooleanType isJoin) {
        return new SearchCommunityResponse(community.getId(), community.getName(), community.getThumbnailImage(),
                community.getFanCount(), isJoin);
    }

    public static List<SearchCommunityResponse> ofList(List<Community> playerCommunities, List<Long> joinCommunityIds) {

        List<SearchCommunityResponse> result = new ArrayList<>();

        for (Community community : playerCommunities) {
            if (joinCommunityIds.contains(community.getId())) {
                SearchCommunityResponse searchCommunityResponse = new SearchCommunityResponse(community.getId(),
                        community.getName(),
                        community.getThumbnailImage(),
                        community.getFanCount(),
                        BooleanType.TRUE);

                result.add(searchCommunityResponse);
            } else {
                SearchCommunityResponse searchCommunityResponse = new SearchCommunityResponse(community.getId(),
                        community.getName(),
                        community.getThumbnailImage(),
                        community.getFanCount(),
                        BooleanType.FALSE);

                result.add(searchCommunityResponse);
            }
        }
        return result;
    }
}
