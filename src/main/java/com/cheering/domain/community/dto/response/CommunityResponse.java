package com.cheering.domain.community.dto.response;

import java.net.URL;

public record CommunityResponse(
        String englishName,
        String koreanName,
        Long fanCount,
        String teamName,
        URL image
) {
}
