package com.cheering.auth.jwt;

import lombok.Builder;

@Builder
public record JWToken(
        String grantType,
        String accessToken,
        String refreshToken
) {
}
