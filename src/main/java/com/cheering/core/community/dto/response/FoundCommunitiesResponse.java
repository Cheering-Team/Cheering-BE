package com.cheering.core.community.dto.response;

import com.cheering.core.community.constant.BooleanType;
import com.cheering.core.community.domain.Community;
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
        BooleanType isJoin,
        List<CommunityResponse> playerCommunities
) {

    public static FoundCommunitiesResponse of(List<CommunityResponse> communityResponse,
                                              Community teamCommunity, BooleanType isJoin) {

        return builder().id(teamCommunity.getId())
                .teamName(teamCommunity.getName())
                .category(teamCommunity.getCategory().getKorean())
                .fanCount(teamCommunity.getFanCount())
                .league(teamCommunity.getLeague().getKorean())
                .image(teamCommunity.getImage())
                .playerCommunities(communityResponse)
                .isJoin(isJoin)
                .build();
    }
}
