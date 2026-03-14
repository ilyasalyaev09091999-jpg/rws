package com.rws.api.rest.archive.dto.filter;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Фильтр запроса для поиска архивных рейсов.
 *
 * <p>Поддерживает новые имена полей ({@code departurePoint}/{@code destinationPoint})
 * и legacy-алиасы ({@code fromCity}/{@code toCity}).</p>
 */
@Data
public class ArchiveSearchFilter {

    private String departurePoint;
    private String destinationPoint;
    private String fromCity;
    private String toCity;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;

    @Min(0)
    private Integer page = 0;

    @Min(1)
    @Max(200)
    private Integer size = 20;

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