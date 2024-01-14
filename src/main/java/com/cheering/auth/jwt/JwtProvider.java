package com.cheering.auth.jwt;

import com.cheering.auth.LoginUserAuthentication;
import com.cheering.user.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtProvider {
    private final Key key;

    private final JwtGenerator jwtGenerator;

    // application.yml에서 secret 값 가져와서 key에 저장
    @Autowired
    public JwtProvider(@Value("${jwt.secret}") String secretKey, JwtGenerator jwtGenerator) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.jwtGenerator = jwtGenerator;
    }

    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // Jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        String id = claims.get("id").toString();

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays
                .stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new LoginUserAuthentication(id, authorities);
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token, HttpServletRequest request, HttpServletResponse response) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            String accessToken = request.getHeader("Access-Token");
            String refreshToken = request.getHeader("Refresh-Token");

            if (accessToken != null && refreshToken == null) {
                request.setAttribute("exception", "expired Access-Token");
            }

            if (refreshToken != null && accessToken == null) {
                request.setAttribute("exception", "expired Refresh-Token");
                Claims claims = parseClaims(refreshToken.substring(7));
                String id = (String) claims.get("id");

                JWToken jwToken = jwtGenerator.generateToken(id,
                        List.of(new SimpleGrantedAuthority(Role.ROLE_USER.name())));

                response.setHeader("Access-Token", jwToken.accessToken());
                response.setHeader("Refresh-Token", jwToken.refreshToken());
            }
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        } catch (SignatureException e) {
            log.info("SignatureException", e);
        }

        return false;
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
