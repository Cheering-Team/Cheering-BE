package com.cheering.user.dto.response;

import com.cheering.community.dto.response.CommunityResponse;

public record UserCommunitiesResponse(
        String profileImage,
        String nickname,
        CommunityResponse community
) {
}
