package com.cheering;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CheeringApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheeringApplication.class, args);
    }

}
