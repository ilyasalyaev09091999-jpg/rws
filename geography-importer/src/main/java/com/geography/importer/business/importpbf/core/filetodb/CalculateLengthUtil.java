package com.geography.importer.business.importpbf.core.filetodb;

import lombok.experimental.UtilityClass;

/**
 * Геодезические расчёты для этапа импорта.
 */
@UtilityClass
public class CalculateLengthUtil {

    /**
     * Вычисляет расстояние между двумя координатами по формуле гаверсинуса.
     *
     * @param lat1 широта первой точки.
     * @param lon1 долгота первой точки.
     * @param lat2 широта второй точки.
     * @param lon2 долгота второй точки.
     * @return расстояние между точками в метрах.
     */
    public double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
