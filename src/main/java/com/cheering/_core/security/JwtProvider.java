package com.cheering._core.security;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cheering.user.User;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.auth0.jwt.JWT;

@Slf4j
@Component
public class JwtProvider {

    public static final Long ACCESS_EXP = 1000L * 60; // 1분
    public static final Long REFRESH_EXP = 1000L * 60 * 60 * 24 * 365; // 1년
    public static final String HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    @Value("${jwt.secret}")
    private String SECRET;

    public String createAccessToken(User user) {
        String jwt = createToken(user, ACCESS_EXP);
        return jwt;
    }

    public String createRefreshToken(User user) {
        String jwt = createToken(user, REFRESH_EXP);
        return jwt;
    }

    public String createToken(User user, Long exp) {
        System.out.println(SECRET);
        return JWT.create()
                .withSubject(user.getPhone())
                .withExpiresAt(new Date(System.currentTimeMillis() + exp))
                .withClaim("id", user.getId())
                .withClaim("role", user.getRole().ordinal())
                .sign(Algorithm.HMAC512(SECRET));
    }

    public DecodedJWT verify(String jwt) throws SignatureVerificationException, TokenExpiredException {
        jwt = jwt.replace(JwtProvider.TOKEN_PREFIX, "");
        return JWT.require(Algorithm.HMAC512(SECRET)).build().verify(jwt);
    }


//    // application.yml에서 secret 값 가져와서 key에 저장
//    @Autowired
//    public JwtProvider(@Value("${jwt.secret}") String secretKey, JwtGenerator jwtGenerator) {
//        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//        this.key = Keys.hmacShaKeyFor(keyBytes);
//        this.jwtGenerator = jwtGenerator;
//    }

//    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
//    public Authentication getAuthentication(String accessToken) {
//        // Jwt 토큰 복호화
//        Claims claims = parseClaims(accessToken);
//
//        if (claims.get("auth") == null) {
//            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
//        }
//
//        String id = claims.get("id").toString();
//
//        // 클레임에서 권한 정보 가져오기
//        Collection<? extends GrantedAuthority> authorities = Arrays
//                .stream(claims.get("auth").toString().split(","))
//                .map(SimpleGrantedAuthority::new)
//                .toList();
//
//        return new CustomUserDetails(id, authorities);
//    }

//    // 토큰 정보를 검증하는 메서드
//    public boolean validateToken(String token) throws ExpiredJwtException {
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(key)
//                    .build()
//                    .parseClaimsJws(token);
//            return true;
//        } catch (SecurityException | MalformedJwtException e) {
//            log.info("Invalid JWT Token", e);
//        } catch (UnsupportedJwtException e) {
//            log.info("Unsupported JWT Token", e);
//        } catch (IllegalArgumentException e) {
//            log.info("JWT claims string is empty.", e);
//        } catch (SignatureException e) {
//            log.info("SignatureException", e);
//        }
//
//        return false;
//    }

//    private Claims parseClaims(String token) {
//        try {
//            return Jwts.parserBuilder()
//                    .setSigningKey(key)
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//        } catch (ExpiredJwtException e) {
//            return e.getClaims();
//        }
//    }

//    public String getAccessToken(String refreshToken) {
//        try {
//            if (refreshToken != null && validateToken(refreshToken)) {
//                log.info("Authenticated User");
//
//                return jwtGenerator.reIssueAccessToken(refreshToken);
//            }
//        } catch (ExpiredJwtException e) {
//            log.error("expired Refresh-Token", e);
//            throw new ExpiredRefreshTokenException(ExceptionMessage.EXPIRED_REFRESH_TOKEN);
//        }
//
//        return null;
//    }

    // Request Header에서 토큰 정보 추출
    public String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtConstant.ACCESS_TOKEN_KEY_NAME);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtConstant.GRANT_TYPE)) {
            return bearerToken.substring(7);
        }

        return null;
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtConstant.REFRESH_TOKEN_KEY_NAME);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtConstant.GRANT_TYPE)) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
