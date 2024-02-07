package com.cheering.domain.community.dto.response;

import com.cheering.domain.community.constant.BooleanType;
import com.cheering.domain.community.domain.Community;
import java.net.URL;
import java.util.List;
import lombok.Builder;

@Builder

public record FoundCommunitiesResponse(
        Long id,
        String teamName,
        String category,
        String league,
        URL image,
        Long fanCount,
        BooleanType isJoin,
        List<SearchCommunityResponse> playerCommunities
) {

    public static FoundCommunitiesResponse of(List<SearchCommunityResponse> searchCommunityResponse,
                                              Community teamCommunity, BooleanType isJoin) {

        return builder().id(teamCommunity.getId())
                .teamName(teamCommunity.getName())
                .category(teamCommunity.getCategory().getKorean())
                .fanCount(teamCommunity.getFanCount())
                .league(teamCommunity.getLeague().getKorean())
                .image(teamCommunity.getImage())
                .playerCommunities(searchCommunityResponse)
                .isJoin(isJoin)
                .build();
    }
}
