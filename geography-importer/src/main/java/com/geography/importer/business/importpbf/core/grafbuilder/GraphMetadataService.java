package com.geography.importer.business.importpbf.core.grafbuilder;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Objects;

/**
 * Постобработка графа маршрутизации: пересчет связных компонент,
 * обновление materialized view и инкремент версии графа.
 */
@Service
@RequiredArgsConstructor
public class GraphMetadataService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Пересчитывает производные метаданные графа поверх уже существующих nodes/edges.
     *
     * @throws IOException если SQL-ресурс не найден или не может быть прочитан.
     */
    @Transactional
    public void rebuildMetadata() throws IOException {
        String sql = new String(
                Objects.requireNonNull(
                        getClass().getClassLoader().getResourceAsStream("sql/rebuild_graph_metadata.sql")
                ).readAllBytes()
        );
        jdbcTemplate.execute(sql);
    }
}
