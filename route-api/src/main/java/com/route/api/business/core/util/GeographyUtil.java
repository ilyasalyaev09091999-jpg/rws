package com.route.api.business.core.util;

/**
 * Географические утилиты для расчетов метрик маршрута.
 */
public class GeographyUtil {

    /**
     * Вычисляет расстояние между двумя координатами по формуле Haversine.
     *
     * @param lat1 широта первой точки в градусах.
     * @param lon1 долгота первой точки в градусах.
     * @param lat2 широта второй точки в градусах.
     * @param lon2 долгота второй точки в градусах.
     * @return расстояние между точками в километрах.
     */
    public static double haversine(
            double lat1, double lon1,
            double lat2, double lon2
    ) {
        final double R = 6371.0;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double rLat1 = Math.toRadians(lat1);
        double rLat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(rLat1) * Math.cos(rLat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Вычисляет угол между двумя векторами в радианах.
     *
     * @param ax x-компонента первого вектора.
     * @param ay y-компонента первого вектора.
     * @param bx x-компонента второго вектора.
     * @param by y-компонента второго вектора.
     * @return угол между векторами в диапазоне [0, pi].
     */
    public static double angleBetween(
            double ax, double ay,
            double bx, double by
    ) {
        double dot = ax * bx + ay * by;
        double lenA = Math.sqrt(ax * ax + ay * ay);
        double lenB = Math.sqrt(bx * bx + by * by);

        if (lenA == 0 || lenB == 0) {
            return Math.PI;
        }

        double cos = dot / (lenA * lenB);
        cos = Math.max(-1.0, Math.min(1.0, cos));

        return Math.acos(cos);
    }
}
