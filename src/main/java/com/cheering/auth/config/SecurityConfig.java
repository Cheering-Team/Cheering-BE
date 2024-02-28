package com.cheering.auth.config;

import com.cheering.auth.jwt.JwtAuthenticationEntryPoint;
import com.cheering.auth.jwt.JwtAuthenticationFilter;
import com.cheering.auth.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final JwtProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // REST API이므로 basic auth 및 csrf 보안을 사용하지 않음
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                // JWT를 사용하기 때문에 세션을 사용하지 않음
                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // 해당 경로의 요청은 명시된 권한 필요
                        .requestMatchers("/api/communities/**").hasRole("USER")
                        .requestMatchers("/api/users/**").hasRole("USER")
                        // 해당 요청은 아무나 접근 가능
                        .requestMatchers("/api/signup").permitAll()
                        .requestMatchers("/api/signin").permitAll()
                        .requestMatchers("/api/set-data").permitAll()
                        .requestMatchers("/api/email").permitAll()
                        .requestMatchers("/api/refresh").permitAll()
                        .anyRequest().permitAll())
                .exceptionHandling(authenticationEntryPoint -> authenticationEntryPoint
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint()))
                // JWT 인증을 위하여 직접 구현한 필터를 UsernamePasswordAuthenticationFilter 전에 실행
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt Encoder 사용
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}



