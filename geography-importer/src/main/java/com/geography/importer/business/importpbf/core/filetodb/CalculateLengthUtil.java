package com.geography.importer.business.importpbf.core.filetodb;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CalculateLengthUtil {

    /**
     * Получить расстояние между нодами в метрах
     *
     * @param lat1 широта 1-й ноды
     * @param lon1 долгота 1-й ноды
     * @param lat2 широта 2-й ноды
     * @param lon2 долгота 2-й ноды
     * @return расстояние между нодами
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
