package com.archive.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа Spring Boot приложения {@code archive-api}.
 */
@SpringBootApplication
public class ArchiveApiApplication {

    /**
     * Запускает контекст Spring Boot.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(ArchiveApiApplication.class, args);
    }
}