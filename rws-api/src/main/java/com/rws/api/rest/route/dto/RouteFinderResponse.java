package com.rws.api.rest.route.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST DTO ответа на запрос построения маршрута.
 *
 * @param duration строковое представление длительности маршрута.
 * @param arrivalDateTime расчётное время прибытия (ETA).
 * @param totalDistance общая длина маршрута.
 * @param route геометрия и узлы маршрута в порядке прохождения.
 * @param routeLocks список шлюзов, встречающихся на маршруте.
 */
public record RouteFinderResponse(
        String duration,
        LocalDateTime arrivalDateTime,
        double totalDistance,
        List<RouteNode> route,
        List<LockDto> routeLocks) {
}
