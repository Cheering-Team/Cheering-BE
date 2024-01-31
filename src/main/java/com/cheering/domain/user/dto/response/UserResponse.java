package com.cheering.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public record UserResponse(Long id,
                           String name) {
}
