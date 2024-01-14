package com.cheering.auth.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "jwt", timeToLive = 30L)
@AllArgsConstructor
public class RedisRefreshTokenDto {
    @Id
    private String accessToken;
    private String refreshToken;
}
