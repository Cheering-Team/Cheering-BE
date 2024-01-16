package com.cheering.auth.redis;

import java.util.List;

public record RedisUserDto(String id, List<String> authorities) {
}
