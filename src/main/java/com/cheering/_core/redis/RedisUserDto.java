package com.cheering._core.redis;

import java.util.List;

public record RedisUserDto(String id, List<String> authorities) {
}
