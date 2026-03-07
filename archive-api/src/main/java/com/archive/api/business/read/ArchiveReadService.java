package com.archive.api.business.read;

import com.archive.api.access_data.db.jpa.model.ArchiveTripEntity;
import com.archive.api.access_data.db.jpa.repository.ArchiveTripJpaRepository;
import com.archive.api.rest.archive.dto.ArchiveRouteStatsItem;
import com.archive.api.rest.archive.dto.ArchiveTripSearchItem;
import com.archive.api.rest.archive.dto.ArchiveTripSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArchiveReadService {

    private final ArchiveTripJpaRepository tripRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ArchiveTripSearchResponse search(String departurePoint,
                                            String destinationPoint,
                                            LocalDate dateFrom,
                                            LocalDate dateTo,
                                            int page,
                                            int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 200);

        PageRequest pageRequest = PageRequest.of(
                safePage,
                safeSize,
                Sort.by(Sort.Order.desc("departureDate"), Sort.Order.desc("id"))
        );

        Page<ArchiveTripEntity> result = tripRepository.search(departurePoint, destinationPoint, dateFrom, dateTo, pageRequest);
        List<ArchiveTripSearchItem> items = result.getContent().stream()
                .map(this::toItem)
                .toList();

        return new ArchiveTripSearchResponse(
                items,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    public List<ArchiveRouteStatsItem> stats(String departurePoint, String destinationPoint, Integer month) {
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

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> mapStats(rs));
    }

    private ArchiveTripSearchItem toItem(ArchiveTripEntity e) {
        return new ArchiveTripSearchItem(
                e.getId(),
                e.getVoyageName(),
                e.getTripType(),
                e.getTugName(),
                e.getDeparturePoint(),
                e.getDestinationPoint(),
                e.getDepartureDate(),
                e.getArrivalDate(),
                e.getDurationDays(),
                e.getCargoType(),
                e.getCargoAmount(),
                e.getDraftM(),
                e.getCounterpartyName(),
                e.getCounterpartyInn(),
                e.getFlag(),
                e.getUnitsCount(),
                e.getRegionFrom(),
                e.getRegionTo()
        );
    }

    private ArchiveRouteStatsItem mapStats(ResultSet rs) throws SQLException {
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
