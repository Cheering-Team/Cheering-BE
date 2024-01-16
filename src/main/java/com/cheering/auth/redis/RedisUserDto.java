package com.cheering.auth.redis;

import com.cheering.user.Role;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public record RedisUserDto(Long id, Role role) {
}
