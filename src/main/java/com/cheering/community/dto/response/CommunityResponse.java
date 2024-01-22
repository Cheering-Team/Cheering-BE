package com.cheering.community.dto.response;

import com.cheering.community.domain.TeamCommunity;
import java.util.List;
import lombok.Builder;

@Builder

public record CommunityResponse(
        Long id,
        String teamName,
        String category,
        String league,
        String image,
        List<PlayerCommunityResponse> playerCommunities
) {

    public static CommunityResponse of(List<PlayerCommunityResponse> playerCommunityResponses,
                                       TeamCommunity teamCommunity) {

        return builder().id(teamCommunity.getId())
                .teamName(teamCommunity.getName())
                .category(teamCommunity.getCategory().getKorean())
                .league(teamCommunity.getLeague().getKorean())
                .image(teamCommunity.getImage())
                .playerCommunities(playerCommunityResponses)
                .build();
    }
}
