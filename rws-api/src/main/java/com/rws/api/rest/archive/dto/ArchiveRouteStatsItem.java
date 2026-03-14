package com.rws.api.rest.archive.dto;

import java.math.BigDecimal;

/**
 * Агрегированная статистика по маршруту архива.
 *
 * @param departurePoint точка отправления (город/порт), по которой группируется статистика
 * @param destinationPoint точка назначения (город/порт), по которой группируется статистика
 * @param departureMonth месяц отправления (1..12), по которому группируется статистика
 * @param tripsCount количество рейсов в группе
 * @param minDays минимальная длительность рейса (дней)
 * @param maxDays максимальная длительность рейса (дней)
 * @param avgDays средняя длительность рейса (дней)
 * @param p50Days медианная длительность рейса (дней)
 * @param p80Days 80-й перцентиль длительности рейса (дней)
 * @param uncertaintyDays оценка неопределенности статистики (в днях)
 */
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