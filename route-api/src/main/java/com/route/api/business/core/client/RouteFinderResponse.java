package com.route.api.business.core.client;

import com.route.api.business.core.enitites.RouteNode;
import com.route.api.business.core.refdata.locks.LockDto;

import java.time.LocalDateTime;
import java.util.List;

public record RouteFinderResponse(
        String duration,
        LocalDateTime arrivalDateTime,
        double totalDistance,
        List<RouteNode> route,
        List<LockDto> routeLocks) {}
