package com.rws.api.rest.archive.dto.filter;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * Фильтр запроса для аналитики архивных рейсов.
 */
@Data
public class ArchiveAnalyticsFilter {

    private String departurePoint;
    private String destinationPoint;
    private String fromCity;
    private String toCity;

    /**
     * Месяц отправления (1..12). {@code null} означает отсутствие фильтра.
     */
    @Min(1)
    @Max(12)
    private Integer month;

    /**
     * Возвращает эффективную точку отправления с fallback на legacy-поле.
     *
     * @return нормализованная точка отправления или {@code null}
     */
    public String effectiveDeparturePoint() {
        return firstNonBlank(departurePoint, fromCity);
    }

    /**
     * Возвращает эффективную точку назначения с fallback на legacy-поле.
     *
     * @return нормализованная точка назначения или {@code null}
     */
    public String effectiveDestinationPoint() {
        return firstNonBlank(destinationPoint, toCity);
    }

    /**
     * Возвращает первое непустое значение из двух кандидатов.
     *
     * @param first первый кандидат
     * @param second второй кандидат
     * @return первое непустое значение или {@code null}
     */
    private String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (second != null && !second.isBlank()) {
            return second;
        }
        return null;
    }
}