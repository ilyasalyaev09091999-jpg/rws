package com.archive.api.access_data.db.jdbc.repository;

import com.archive.api.business.read.dto.ArchiveRouteStatsItem;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ArchiveRouteStatsRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<ArchiveRouteStatsItem> findStats(String departurePoint, String destinationPoint, Integer month) {
        String sql = """
                select departure_point,
                       destination_point,
                       departure_month,
                       trips_count,
                       min_days,
                       max_days,
                       avg_days,
                       p50_days,
                       p80_days,
                       uncertainty_days
                from v_archive_route_stats
                where (:departurePoint is null or lower(departure_point) = lower(:departurePoint))
                  and (:destinationPoint is null or lower(destination_point) = lower(:destinationPoint))
                  and departure_month = coalesce(cast(:month as int), departure_month)
                order by trips_count desc, departure_point, destination_point, departure_month
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("departurePoint", departurePoint)
                .addValue("destinationPoint", destinationPoint)
                .addValue("month", month);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> mapRow(rs));
    }

    private ArchiveRouteStatsItem mapRow(ResultSet rs) throws SQLException {
        return new ArchiveRouteStatsItem(
                rs.getString("departure_point"),
                rs.getString("destination_point"),
                rs.getInt("departure_month"),
                rs.getLong("trips_count"),
                rs.getInt("min_days"),
                rs.getInt("max_days"),
                rs.getBigDecimal("avg_days"),
                rs.getBigDecimal("p50_days"),
                rs.getBigDecimal("p80_days"),
                rs.getBigDecimal("uncertainty_days")
        );
    }
}
