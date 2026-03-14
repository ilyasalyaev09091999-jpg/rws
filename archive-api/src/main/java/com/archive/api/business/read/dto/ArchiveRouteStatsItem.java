package com.archive.api.business.read.dto;

import java.math.BigDecimal;

/**
 * Агрегированная статистика по маршруту архива.
 *
 * @param departurePoint точка отправления (город/порт)
 * @param destinationPoint точка назначения (город/порт)
 * @param departureMonth месяц отправления (1..12)
 * @param tripsCount количество рейсов
 * @param minDays минимальная длительность (дни)
 * @param maxDays максимальная длительность (дни)
 * @param avgDays средняя длительность (дни)
 * @param p50Days медиана длительности (дни)
 * @param p80Days 80-й перцентиль длительности (дни)
 * @param uncertaintyDays оценка неопределенности (дни)
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