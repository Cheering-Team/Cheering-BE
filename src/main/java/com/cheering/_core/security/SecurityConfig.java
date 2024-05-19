package com.cheering._core.security;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering._core.util.FilterResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
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
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // 해당 경로의 요청은 명시된 권한 필요
//                        .requestMatchers("/api/communities/**").hasRole("USER")
//                        .requestMatchers("/api/users/**").hasRole("USER")
                        // 해당 요청은 아무나 접근 가능
                        .requestMatchers("/api/phone/sms").permitAll()
                        .requestMatchers("/api/phone/code").permitAll()
                        .requestMatchers("/api/signup").permitAll()
                        .requestMatchers("/api/signin").permitAll()
                        .requestMatchers("/api/set-data").permitAll()
                        .requestMatchers("/api/refresh").permitAll()
                        .requestMatchers("/hc").permitAll()
                        .requestMatchers("/env").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(((request, response, authException) -> {
                    FilterResponseUtils.unAuthorized(response, new CustomException(ExceptionCode.USER_UNAUTHORIZED));
                })))
                .exceptionHandling(exception -> exception.accessDeniedHandler(((request, response, authException) -> {
                    FilterResponseUtils.forbidden(response, new CustomException(ExceptionCode.USER_FORBIDDEN));
                })))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt Encoder 사용
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}



