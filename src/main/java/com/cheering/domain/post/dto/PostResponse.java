package com.cheering.domain.post.dto;

import lombok.Builder;

@Builder
public record PostResponse(Long id,
                           String content) {
}
