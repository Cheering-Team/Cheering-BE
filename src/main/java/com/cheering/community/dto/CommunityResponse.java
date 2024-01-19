package com.cheering.community.dto;

import com.cheering.community.constant.Category;
import com.cheering.community.constant.League;

public record CommunityResponse(
        Long id,
        String teamName,
        Category category,
        League league,
        String image,
        PlayerCommunityResponse playerCommunities
) {
}
