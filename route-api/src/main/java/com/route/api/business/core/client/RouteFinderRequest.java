package com.route.api.business.core.client;

import java.time.LocalDateTime;

/**
 * DTO-запрос на базовый расчёт маршрута.
 * Используется для передачи исходных параметров маршрута от UI или внешней системы.
 *
 * @param startLongitude Широта точки отправления
 * @param startLatitude Долгота точки отправления
 * @param endLongitude Широта точки отправления
 * @param endLatitude Долгота точки назначения
 * @param departureTime Желаемое время отправления маршрута.
 * @param speed Средняя скорость по маршруту
 */
public record RouteFinderRequest(
        double startLongitude,
        double startLatitude,
        double endLongitude,
        double endLatitude,
        LocalDateTime departureTime,
        int speed) {
}
