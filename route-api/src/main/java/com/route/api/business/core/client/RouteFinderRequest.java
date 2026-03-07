package com.route.api.business.core.client;

import java.time.LocalDateTime;

/**
 * DTO запроса на построение маршрута в {@code route-api}.
 *
 * @param startLongitude долгота точки отправления.
 * @param startLatitude широта точки отправления.
 * @param endLongitude долгота точки назначения.
 * @param endLatitude широта точки назначения.
 * @param departureTime время отправления, используемое в расчете ETA.
 * @param speed средняя скорость судна в км/ч.
 */
public record RouteFinderRequest(
        double startLongitude,
        double startLatitude,
        double endLongitude,
        double endLatitude,
        LocalDateTime departureTime,
        int speed) {
}
