package com.refdata.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа микросервиса {@code refdata-api}.
 * <p>
 * Сервис отдает справочные данные (порты и шлюзы) по gRPC другим
 * микросервисам. В системе поддерживаются две отдельные проекции данных:
 * </p>
 * <ul>
 *   <li>расширенная DTO-модель для {@code rws-api},</li>
 *   <li>компактная DTO-модель для {@code route-api}.</li>
 * </ul>
 */
@SpringBootApplication
public class RefDataApiApplication {

    /**
     * Запускает Spring Boot приложение {@code refdata-api}.
     *
     * @param args аргументы командной строки.
     */
    public static void main(String[] args) {
        SpringApplication.run(RefDataApiApplication.class, args);
    }
}
