package com.rws.api.rest.archive.dto.filter;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * Фильтр REST-запроса для аналитики архивных рейсов.
 */
@Data
public class ArchiveAnalyticsFilter {

    private String departurePoint;
    private String destinationPoint;
    private String fromCity;
    private String toCity;

    /**
     * Номер месяца отправления (1..12). Значение {@code null} означает "без фильтра".
     */
    @Min(1)
    @Max(12)
    private Integer month;

    /**
     * Возвращает нормализованную точку отправления с fallback на legacy поле.
     */
    public String effectiveDeparturePoint() {
        return firstNonBlank(departurePoint, fromCity);
    }

    /**
     * Возвращает нормализованную точку назначения с fallback на legacy поле.
     */
    public String effectiveDestinationPoint() {
        return firstNonBlank(destinationPoint, toCity);
    }

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
