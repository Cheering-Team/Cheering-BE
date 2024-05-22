package com.cheering._core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", "401");
        responseData.put("message", "토큰이 유효하지 않습니다.");
        responseData.put("result", null);

        response.setContentType("applicaton/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(401);
        response.getWriter().write(objectMapper.writeValueAsString(responseData));
    }
}
