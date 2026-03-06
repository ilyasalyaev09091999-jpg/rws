package com.rws.api.rest.route.dto;

import java.time.LocalDateTime;

/**
 * REST DTO запроса на построение маршрута.
 *
 * @param startLongitude долгота точки отправления.
 * @param startLatitude широта точки отправления.
 * @param endLongitude долгота точки назначения.
 * @param endLatitude широта точки назначения.
 * @param departureTime время отправления, используемое для расчёта ETA.
 * @param speed скорость движения по маршруту.
 */
public record RouteFinderRequest(
        double startLongitude,
        double startLatitude,
        double endLongitude,
        double endLatitude,
        LocalDateTime departureTime,
        int speed) {
}
