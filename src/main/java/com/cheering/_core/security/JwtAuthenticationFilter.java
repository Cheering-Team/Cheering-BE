package com.cheering._core.security;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cheering.user.Role;
import com.cheering.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String jwt = request.getHeader(JwtProvider.HEADER);

        if (jwt == null) {
            chain.doFilter(request, response);
            return;
        }

        try {
            DecodedJWT decodedJWT = jwtProvider.verify(jwt);
            Long id = decodedJWT.getClaim("id").asLong();
            Role role = decodedJWT.getClaim("role").as(Role.class);
            User user = User.builder().userId(id).role(role).build();
            CustomUserDetails myUserDetails = new CustomUserDetails(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(myUserDetails, myUserDetails.getPassword(), myUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (SignatureVerificationException sve) {
            System.out.println("유효하지않는 토큰");
        } catch (TokenExpiredException tee) {
            System.out.println("토큰 만료");
        } finally {
            chain.doFilter(request, response);
        }
    }
}
