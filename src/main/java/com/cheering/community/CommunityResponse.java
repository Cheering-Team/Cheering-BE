package com.cheering.community;

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
