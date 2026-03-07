package com.rws.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа сервиса {@code rws-api}.
 * <p>
 * {@code rws-api} является внешним REST API (API Gateway) системы Routing
 * Waterway System. Сервис принимает HTTP-запросы клиентов, обращается к
 * внутренним микросервисам по gRPC (в первую очередь к {@code route-api} и
 * {@code refdata-api}), агрегирует результаты и возвращает DTO,
 * ориентированные на внешний контракт.
 * </p>
 * <p>
 * Аннотация {@link SpringBootApplication} включает автоконфигурацию Spring,
 * компонент-сканирование и запуск контейнера приложения.
 * </p>
 */
@SpringBootApplication
public class RwsApiApplication {

    /**
     * Запускает Spring Boot приложение {@code rws-api}.
     *
     * @param args аргументы командной строки.
     */
    public static void main(String[] args) {
        SpringApplication.run(RwsApiApplication.class, args);
    }
}
