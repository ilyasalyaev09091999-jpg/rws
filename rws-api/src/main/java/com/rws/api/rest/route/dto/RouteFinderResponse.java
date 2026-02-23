package com.rws.api.rest.route.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RouteFinderResponse(
        String duration,
        LocalDateTime arrivalDateTime,
        double totalDistance,
        List<RouteNode> route,
        List<LockDto> routeLocks) {}
