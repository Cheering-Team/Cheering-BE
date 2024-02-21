package com.cheering.domain.post.dto;

import java.net.URL;
import lombok.Builder;

@Builder
public record FileInfo(
        URL url,
        int width,
        int height
) {
}
