package com.cheering._core.security;

import static com.cheering._core.security.JwtConstant.ACCESS_TOKEN_EXPIRE_TIME;
import static com.cheering._core.security.JwtConstant.GRANT_TYPE;
import static com.cheering._core.security.JwtConstant.REFRESH_TOKEN_EXPIRE_TIME;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtGenerator {
//    private final Key key;
//    private final RedisRepository redisRepository;
//
//    // application.yml에서 secret 값 가져와서 key에 저장
//    @Autowired
//    public JwtGenerator(@Value("${jwt.secret}") String secretKey,
//                        RedisRepository redisRepository) {
//        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//        this.key = Keys.hmacShaKeyFor(keyBytes);
//        this.redisRepository = redisRepository;
//    }

    // User 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
//    public JWToken generateToken(String id, Collection<? extends GrantedAuthority> roles) {
//        //권한 문자열 변환
//        List<String> authorities = roles.stream()
//                .map(GrantedAuthority::getAuthority)
//                .toList();
//
//        long now = (new Date()).getTime();
//        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
//
//        // Access Token 생성
//        String accessToken = generateAccessToken(id, authorities, accessTokenExpiresIn);
//
//        // Refresh Token 생성
//        String refreshToken = generateRefreshToken(now, id);
//
//        RedisUserDto redisUserDto = new RedisUserDto(id, authorities);
//        redisRepository.set(refreshToken, redisUserDto, REFRESH_TOKEN_EXPIRE_TIME / 1000);
//
//        return JWToken.builder()
//                .grantType(GRANT_TYPE)
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .build();
//    }

//    public String reIssueAccessToken(String refreshToken) {
//        RedisUserDto redisUserDto = redisRepository.get(refreshToken);
//
//        if (redisUserDto != null) {
//            long now = (new Date()).getTime();
//            Date expireTime = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
//
//            return generateAccessToken(redisUserDto.id(), redisUserDto.authorities(), expireTime);
//        }
//
//        return null;
//    }

//    private String generateAccessToken(String id, List<String> authorities, Date expireTime) {
//
//        return Jwts.builder()
//                .claim("auth", String.join(",", authorities))
//                .claim("id", id)
//                .setExpiration(expireTime)
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    private String generateRefreshToken(long now, String id) {
//        return Jwts.builder()
//                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//    }
}
