package com.cheering.domain.community.dto.response;

import java.net.URL;
import lombok.Builder;

@Builder
public record CommunityResponse(
        String englishName,
        String koreanName,
        Long fanCount,
        String teamName,
        URL backgroundImage
) {
}
