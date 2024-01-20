package com.cheering.community.dto.response;

import com.cheering.community.constant.Category;
import com.cheering.community.constant.League;
import com.cheering.community.domain.TeamCommunity;
import java.util.List;
import lombok.Builder;

@Builder

public record CommunityResponse(
        Long id,
        String teamName,
        Category category,
        League league,
        String image,
        List<PlayerCommunityResponse> playerCommunities
) {

    public static CommunityResponse of(List<PlayerCommunityResponse> playerCommunityResponses,
                                       TeamCommunity teamCommunity) {

        return builder().id(teamCommunity.getId())
                .teamName(teamCommunity.getName())
                .category(teamCommunity.getCategory())
                .league(teamCommunity.getLeague())
                .image(teamCommunity.getImage())
                .playerCommunities(playerCommunityResponses)
                .build();
    }
}
