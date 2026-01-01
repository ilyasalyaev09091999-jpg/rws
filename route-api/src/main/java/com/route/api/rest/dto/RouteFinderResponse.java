package com.route.api.rest.dto;

import com.route.api.business.core.enitites.RouteNode;
import java.time.LocalDateTime;
import java.util.List;

public record RouteFinderResponse(
        String duration,
        LocalDateTime arrivalDateTime,
        double totalDistance,
        List<RouteNode> route) {}
