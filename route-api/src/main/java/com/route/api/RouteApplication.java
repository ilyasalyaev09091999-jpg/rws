package com.route.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Точка входа Spring Boot приложения route-api.
 */
@EnableCaching
@SpringBootApplication
public class RouteApplication {

    /**
     * Запускает приложение route-api.
     */
    public static void main(String[] args) {
        SpringApplication.run(RouteApplication.class, args);
    }
}
