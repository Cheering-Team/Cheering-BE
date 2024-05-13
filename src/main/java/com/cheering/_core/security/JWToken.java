package com.cheering._core.security;

import lombok.Builder;

@Builder
public record JWToken(
        String grantType,
        String accessToken,
        String refreshToken
) {
}
