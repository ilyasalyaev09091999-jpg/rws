package com.route.api.access_data.db.jdbc.repository;

import com.route.api.business.core.enitites.RouteNode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PgRoutingRepository {

    private final JdbcTemplate jdbcTemplate;


    public Long findNearestNodeId(double startLon, double startLat) {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM nodes ORDER BY geom <-> ST_SetSRID(ST_MakePoint(?, ?), 4326) LIMIT 1",
                Long.class,
                startLon, startLat
        );
    }


    public List<RouteNode> findRoute(Long sourceNode, Long targetNode, double[] bBox) {
        // bBox: [minLat, minLon, maxLat, maxLon]
        double minLat = bBox[0];
        double minLon = bBox[1];
        double maxLat = bBox[2];
        double maxLon = bBox[3];

        // Формируем ST_MakeEnvelope с правильной локалью
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
     * Строит маршрут между узлами sourceNode и targetNode без bbox.
     * Используется для port-to-port или глобальных маршрутов.
     *
     * @param sourceNode ID узла отправления
     * @param targetNode ID узла назначения
     * @return список узлов маршрута
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
