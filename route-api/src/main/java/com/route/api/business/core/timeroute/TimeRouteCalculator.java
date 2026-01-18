package com.route.api.business.core.timeroute;

import com.route.api.business.core.enitites.RouteNode;
import com.route.api.business.core.util.GeographyUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeRouteCalculator {

    /**
     * Расет времени маршрута
     *
     * @param route список узлов маршрута
     * @param avgSpeedKmh средняя скорость из запроса в км/ч
     * @param locksCount количество шлюзов на маршруте
     * @return время маршрута в секундах
     */
    public long calculate(List<RouteNode> route, int avgSpeedKmh, int locksCount) {

        // Рассчитывае сначала нормальное время маршрута l/v
        double totalDistanceKm = route.get(route.size() - 1).cost() / 1000.0;
        long baseTimeSec = Math.round(
                (totalDistanceKm / avgSpeedKmh) * 3600
        );

        double curvature = calculateCurvature(route, totalDistanceKm);
        double turnIntensity = calculateTurnIntensity(route, totalDistanceKm);
        double K = calculateCorrectionFactor(curvature, turnIntensity);

        double correctedTime = baseTimeSec * K;


        for (int i = 0; i < locksCount; i++) {
            correctedTime += 7200;
        }

        return Math.round(correctedTime);
    }


    /**
     * Вычисляет коэффициент извилистости маршрута.
     * <p>
     * Коэффициент извилистости (curvature) определяется как отношение
     * общей длины маршрута к прямой (кратчайшей) дистанции между начальной
     * и конечной точками маршрута. Значение ≥ 1:
     * - 1 → почти прямой маршрут,
     * - >1 → маршрут извилистый.
     * <p>
     * Поскольку фактическая длина маршрута уже учитывает геометрию водного пути,
     * коэффициент извилистости используется не как линейный множитель времени,
     * а как ограниченный индикатор сложности маршрута, отражающий потенциальное
     * снижение средней скорости движения.
     *
     * @param route список узлов маршрута, каждый элемент содержит lat/lon
     * @param totalDistanceKm общая длина маршрута в километрах
     * @return коэффициент извилистости маршрута (безразмерный), минимум 1.0
     */
    private double calculateCurvature(List<RouteNode> route, double totalDistanceKm) {
        double straightDistance = GeographyUtil.haversine(
                route.get(0).lat(), route.get(0).lon(),
                route.get(route.size() - 1).lat(), route.get(route.size() - 1).lon()
        );

        double curvature = totalDistanceKm / straightDistance;
        return Math.min(curvature - 1.0, 0.3);
    }


    /**
     * Вычисляет суммарную интенсивность поворотов для маршрута.
     * <p>
     * Алгоритм проходит по всем тройкам последовательных узлов маршрута A–B–C
     * и вычисляет угол поворота в каждой точке B. Угол считается между векторами
     * BA (из B в A) и BC (из B в C), чтобы определить отклонение от прямого движения.
     * Чем больше суммарный угол, тем извилистее маршрут.
     * <p>
     * Суммарная интенсивность поворотов использовуется как компонент
     * корректирующего коэффициента для расчёта времени движения по маршруту.
     *
     * @param route список узлов маршрута, каждый элемент содержит lat/lon
     * @return суммарная интенсивность поворотов в радианах
     */
    private double calculateTurnIntensity(List<RouteNode> route, double totalDistanceKm) {
        double totalTurn = 0.0;

        for (int i = 1; i < route.size() - 1; i++) {
            RouteNode a = route.get(i - 1);
            RouteNode b = route.get(i);
            RouteNode c = route.get(i + 1);

            double v1x = a.lat() - b.lat();
            double v1y = a.lon() - b.lon();
            double v2x = c.lat() - b.lat();
            double v2y = c.lon() - b.lon();

            double angle = GeographyUtil.angleBetween(v1x, v1y, v2x, v2y);
            totalTurn += Math.abs(Math.PI - angle);
        }

        // НОРМИРОВКА
        return totalTurn / totalDistanceKm; // рад/км
    }



    /**
     * Вычисляет корректирующий коэффициент K для расчёта времени маршрута.
     * <p>
     * Коэффициент K учитывает сложность маршрута:
     * - извилистость пути (curvature),
     * - интенсивность поворотов (turnIntensity).
     * <p>
     * Формула:
     * <pre>
     *     K = 1 + α * (curvature - 1) + β * turnIntensity
     * </pre>
     * где α и β — веса, задающие влияние извилистости и поворотов на итоговое время.
     * Значение K ограничено диапазоном [1.0, 2.0]:
     * - 1.0 → прямой маршрут без поворотов,
     * - >1.0 → маршрут сложнее, время увеличивается.
     * <p>
     * Коэффициент K используется для корректировки базового времени движения:
     * <pre>
     *     correctedTime = baseTime * K
     * </pre>
     *
     * @param curvature коэффициент извилистости маршрута (≥ 1)
     * @param turnIntensity суммарная интенсивность поворотов маршрута (рад/км)
     * @return корректирующий коэффициент K (безразмерный, диапазон [1.0, 2.0])
     */
    private double calculateCorrectionFactor(double curvature, double turnIntensity) {
        double alpha = 0.15; // вес извилистости
        double beta  = 0.2; // вес поворотов

        double K = 1.0 + alpha * curvature + beta * turnIntensity;

        // защита от слишком маленьких или больших значений
        K = Math.max(1.0, Math.min(K, 2.0));
        return K;
    }
}
