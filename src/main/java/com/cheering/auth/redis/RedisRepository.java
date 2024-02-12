package com.cheering.auth.redis;

import static com.cheering.auth.jwt.JwtConstant.REFRESH_TOKEN_EXPIRE_TIME;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
public class RedisRepository {

    private final ValueOperations<String, Object> redis;
    private final ObjectMapper mapper;

    public RedisRepository(RedisTemplate<String, Object> redisTemplate, ObjectMapper mapper) {
        this.redis = redisTemplate.opsForValue();
        this.mapper = mapper;
    }

    public void set(String key, RedisUserDto dto, long expireTime) {
        try {
            String value = mapper.writeValueAsString(dto);
            redis.set(key, value, REFRESH_TOKEN_EXPIRE_TIME / 1000);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public RedisUserDto get(String key) {
        String keyString = (String) redis.get(key);
        if (keyString != null) {
            String seperatedKey = keyString.replaceAll("\\x00", "");
            try {
                return mapper.readValue(seperatedKey, RedisUserDto.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    public String delete(String key) {
        return (String) redis.getAndDelete(key);
    }
}