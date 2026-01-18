package com.route.api.business.core.util;

public class GeographyUtil {

    /**
     * Вычисляет расстояние между двумя точками на поверхности Земли по формуле Haversine.
     * <p>
     * Формула учитывает кривизну Земли и возвращает длину кратчайшей дуги
     * большого круга между точками с заданными широтой и долготой.
     * <p>
     * Используется для расчёта прямой дистанции между начальной и конечной
     * точками маршрута при вычислении извилистости.
     *
     * @param lat1 широта первой точки в градусах
     * @param lon1 долгота первой точки в градусах
     * @param lat2 широта второй точки в градусах
     * @param lon2 долгота второй точки в градусах
     * @return расстояние между точками в километрах
     */
    public static double haversine(
            double lat1, double lon1,
            double lat2, double lon2
    ) {
        final double R = 6371.0; // радиус Земли в км

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
     * Вычисляет угол поворота в точке B между последовательными сегментами маршрута A–B–C.
     * Угол определяется между векторами BA и BC, т.е. между направлением движения
     * из точки A в точку B и направлением движения из точки B в точку C.
     *
     * @param ax x-компонента вектора BA (A − B)
     * @param ay y-компонента вектора BA (A − B)
     * @param bx x-компонента вектора BC (C − B)
     * @param by y-компонента вектора BC (C − B)
     * @return угол между векторами BA и BC в радианах
     */
    public static double angleBetween(
            double ax, double ay,
            double bx, double by
    ) {
        double dot = ax * bx + ay * by;
        double lenA = Math.sqrt(ax * ax + ay * ay);
        double lenB = Math.sqrt(bx * bx + by * by);

        if (lenA == 0 || lenB == 0) {
            return Math.PI; // считаем прямым ходом
        }

        double cos = dot / (lenA * lenB);

        // защита от численных ошибок
        cos = Math.max(-1.0, Math.min(1.0, cos));

        return Math.acos(cos);
    }

}
