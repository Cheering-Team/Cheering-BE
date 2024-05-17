package com.cheering._core.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 1. Request Header에서 JWT 토큰 추출
        String accessToken = jwtTokenProvider.resolveAccessToken(request);

        // 2. validateToken으로 토큰 유효성 검사
//        try {
//            if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
//                // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
//                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//                log.info("Authenticated User");
//            }
//        } catch (ExpiredJwtException e) {
//            //엑세스 토큰이 만료된 경우
//            log.error("Expired JWT Token", e);
//            request.setAttribute("exception", "expired Access-Token");
//        } finally {
//            filterChain.doFilter(request, response);
//        }
        filterChain.doFilter(request, response);
    }
}
