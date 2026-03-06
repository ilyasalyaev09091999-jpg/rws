package com.geography.importer.business.importpbf.core.grafbuilder;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

/**
 * Сервис постобработки импортированных данных: строит рёбра графа маршрутизации.
 * <p>
 * Загружает SQL-скрипт {@code classpath:sql/build_edges.sql}, который
 * очищает таблицу {@code edges}, пересобирает её из {@code way_nodes/nodes}
 * и обновляет materialized view для ускоренного A* поиска.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class GraphBuilderService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Выполняет SQL-скрипт построения рёбер графа.
     *
     * @throws IOException если SQL-ресурс не найден или не может быть прочитан.
     */
    public void buildEdges() throws IOException {

        System.out.println("buildEdges");

        String sql = new String(
                Objects.requireNonNull(
                        getClass().getClassLoader().getResourceAsStream("sql/build_edges.sql")
                ).readAllBytes()
        );
        jdbcTemplate.execute(sql);

        System.out.println("buildEdges fin");
    }
}
