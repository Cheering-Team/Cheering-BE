package com.cheering._core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.servers.Server;



@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cheering API")
                        .description("API documentation for Cheering Application")
                        .version("v1.0"))
                .addServersItem(new Server().url("http://localhost:8080").description("Local Server"));
    }
}

