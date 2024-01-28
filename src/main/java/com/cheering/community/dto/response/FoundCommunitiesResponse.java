package com.cheering.community.dto.response;

import java.util.List;
import lombok.Builder;

@Builder

public record FoundCommunitiesResponse(
        Long id,
        String teamName,
        String category,
        String league,
        String image,
        Long fanCount,
        List<CommunityResponse> playerCommunities
) {

    public static FoundCommunitiesResponse of(List<CommunityResponse> communityResponse,
                                              TeamCommunity teamCommunity) {

        return builder().id(teamCommunity.getId())
                .teamName(teamCommunity.getName())
                .category(teamCommunity.getCategory().getKorean())
                .fanCount(teamCommunity.getFanCount())
                .league(teamCommunity.getLeague().getKorean())
                .image(teamCommunity.getImage())
                .playerCommunities(communityResponse)
                .build();
    }
}
