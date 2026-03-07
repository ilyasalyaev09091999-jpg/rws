package com.archive.api.rest.archive.dto;

import java.math.BigDecimal;

public record ArchiveRouteStatsItem(
        String departurePoint,
        String destinationPoint,
        Integer departureMonth,
        Long tripsCount,
        Integer minDays,
        Integer maxDays,
        BigDecimal avgDays,
        BigDecimal p50Days,
        BigDecimal p80Days,
        BigDecimal uncertaintyDays
) {
}
