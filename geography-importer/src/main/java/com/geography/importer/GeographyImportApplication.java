package com.geography.importer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа сервиса {@code geography-importer}.
 * <p>
 * Сервис импортирует навигационные данные из файла {@code .osm.pbf}
 * в базу {@code geography}, после чего формирует рёбра графа маршрутизации,
 * используемого downstream-сервисами (в первую очередь {@code route-api}).
 * </p>
 */
@SpringBootApplication
public class GeographyImportApplication {

    /**
     * Запускает Spring Boot приложение импорта геоданных.
     *
     * @param args аргументы командной строки.
     */
    public static void main(String[] args) {
        SpringApplication.run(GeographyImportApplication.class, args);
    }
}
