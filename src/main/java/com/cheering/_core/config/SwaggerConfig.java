package com.cheering._core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Security 설정 추가
        SecurityScheme securityScheme = new SecurityScheme()
                .name("Authorization")
                .type(Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(In.HEADER);

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("Authorization"))
                .components(new io.swagger.v3.oas.models.Components().addSecuritySchemes("Authorization", securityScheme))
                .info(new Info().title("Cheering API").version("1.0.0").description("API Documentation with JWT Authentication"));
    }
}
