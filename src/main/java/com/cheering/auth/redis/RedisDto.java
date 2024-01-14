package com.cheering.auth.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "jwt", timeToLive = 30L)
@AllArgsConstructor
public class RedisDto {

    @Id
    private String refreshToken;
    private RedisUserDto redisUserDto;
}
