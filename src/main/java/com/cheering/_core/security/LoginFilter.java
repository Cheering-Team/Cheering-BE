package com.cheering._core.security;

import com.cheering._core.util.RedisUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@AllArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RedisUtils redisUtils;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String username = obtainUsername(request);
        String code = obtainPassword(request);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, code, null);

        return authenticationManager.authenticate(authToken);
    }
    @Override
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter("phone");
    }

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        return request.getParameter("code");
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String phone = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends  GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        String accessToken = jwtUtil.createJwt(phone, role, 1000 * 60 * 60 * 24L);
        String refreshToken = jwtUtil.createJwt(phone, role, 1000 * 60 * 60 * 24 * 30L);

        redisUtils.setDataExpire(customUserDetails.getUser().getId().toString(), refreshToken, 1000 * 60 * 60 * 24 * 30L);

        Map<String, Object> responseData = new HashMap<>();
        Map<String, Object> responseResult = new HashMap<>();

        responseResult.put("accessToken", accessToken);
        responseResult.put("refreshToken", refreshToken);

        responseData.put("code", HttpServletResponse.SC_OK);
        responseData.put("message", "로그인에 성공하였습니다.");
        responseData.put("result", responseResult);


        response.setContentType("applicaton/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        response.getWriter().write(objectMapper.writeValueAsString(responseData));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        Map<String, Object> responseData = new HashMap<>();

        responseData.put("message", failed.getMessage());
        responseData.put("result", null);
        responseData.put("code", 400);

        response.setContentType("applicaton/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(400);
        response.getWriter().write(objectMapper.writeValueAsString(responseData));
    }
}
