package com.cheering.domain.post.dto;

import java.net.URL;

public record FileInfo(
        URL url,
        int width,
        int height
) {
}
