package com.cheering.auth.jwt;

import lombok.Builder;

@Builder
public record JwtDto(
        String grantType,
        String accessToken,
        String refreshToken
) {
}
