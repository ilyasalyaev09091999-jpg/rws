package com.route.api.access_data.db.jdbc.repository;

import com.route.api.business.core.enitites.RouteNode;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * Репозиторий SQL-запросов к графу водных путей (PostgreSQL + pgRouting).
 */
@Service
@RequiredArgsConstructor
public class PgRoutingRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Находит ближайшую ноду графа к указанным координатам.
     *
     * @param startLon долгота точки.
     * @param startLat широта точки.
     * @return идентификатор ближайшей ноды.
     */
    public Long findNearestNodeId(double startLon, double startLat) {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM nodes ORDER BY geom <-> ST_SetSRID(ST_MakePoint(?, ?), 4326) LIMIT 1",
                Long.class,
                startLon, startLat
        );
    }

    /**
     * Строит маршрут между узлами с пространственным ограничением по bbox.
     *
     * @param sourceNode ID узла отправления.
     * @param targetNode ID узла назначения.
     * @param bBox ограничивающий прямоугольник [minLat, minLon, maxLat, maxLon].
     * @return список узлов маршрута в порядке прохождения.
     */
    public List<RouteNode> findRoute(Long sourceNode, Long targetNode, double[] bBox) {
        double minLat = bBox[0];
        double minLon = bBox[1];
        double maxLat = bBox[2];
        double maxLon = bBox[3];

        String bbox1 = String.format(Locale.US, "ST_MakeEnvelope(%f, %f, %f, %f, 4326)", minLon, minLat, maxLon, maxLat);
        String bbox2 = String.format(Locale.US, "ST_MakeEnvelope(%f, %f, %f, %f, 4326)", minLon, minLat, maxLon, maxLat);

        String sql = String.format("""
        SELECT
            r.seq,
            r.node AS node_id,
            ST_Y(n.geom) AS lat,
            ST_X(n.geom) AS lon,
            r.agg_cost AS cost
        FROM pgr_astar(
            '
            SELECT
                id,
                source,
                target,
                cost,
                x1,
                y1,
                x2,
                y2
            FROM edges_astar
            WHERE
                geom1 && %s
                AND geom2 && %s
            ',
            ?, ?, directed := false
        ) r
        JOIN nodes n ON n.id = r.node
        ORDER BY r.seq
        """, bbox1, bbox2);

        return jdbcTemplate.query(
                sql,
                ps -> {
                    ps.setLong(1, sourceNode);
                    ps.setLong(2, targetNode);
                },
                (rs, rowNum) -> new RouteNode(
                        rs.getInt("seq"),
                        rs.getLong("node_id"),
                        rs.getDouble("lat"),
                        rs.getDouble("lon"),
                        rs.getDouble("cost")
                )
        );
    }

    /**
     * Строит маршрут между узлами без ограничения bbox.
     * <p>
     * Используется в сценариях стабильных дальних маршрутов, например порт-порт.
     * </p>
     *
     * @param sourceNode ID узла отправления.
     * @param targetNode ID узла назначения.
     * @return список узлов маршрута в порядке прохождения.
     */
    public List<RouteNode> findRoute(Long sourceNode, Long targetNode) {

        String sql = """
        SELECT
            r.seq,
            r.node AS node_id,
            ST_Y(n.geom) AS lat,
            ST_X(n.geom) AS lon,
            r.agg_cost AS cost
        FROM pgr_astar(
            '
            SELECT
                id,
                source,
                target,
                cost,
                x1,
                y1,
                x2,
                y2
            FROM edges_astar
            ',
            ?, ?, directed := false
        ) r
        JOIN nodes n ON n.id = r.node
        ORDER BY r.seq
        """;

        return jdbcTemplate.query(
                sql,
                ps -> {
                    ps.setLong(1, sourceNode);
                    ps.setLong(2, targetNode);
                },
                (rs, rowNum) -> new RouteNode(
                        rs.getInt("seq"),
                        rs.getLong("node_id"),
                        rs.getDouble("lat"),
                        rs.getDouble("lon"),
                        rs.getDouble("cost")
                )
        );
    }
}
