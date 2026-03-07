package com.route.api.business.core.timeroute;

import com.route.api.business.core.enitites.RouteNode;
import com.route.api.business.core.util.GeographyUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис расчета времени прохождения маршрута.
 * <p>
 * Базовая оценка времени считается как {@code distance / speed}, после чего
 * корректируется коэффициентом сложности маршрута (извилистость + повороты)
 * и фиксированной задержкой на шлюзы.
 * </p>
 */
@Service
public class TimeRouteCalculator {

    /**
     * Рассчитывает время маршрута в секундах.
     *
     * @param route список узлов маршрута в порядке прохождения.
     * @param avgSpeedKmh средняя скорость движения в км/ч.
     * @param locksCount количество шлюзов на маршруте.
     * @return итоговое время маршрута в секундах.
     */
    public long calculate(List<RouteNode> route, int avgSpeedKmh, int locksCount) {
        double totalDistanceKm = route.get(route.size() - 1).cost() / 1000.0;
        long baseTimeSec = Math.round((totalDistanceKm / avgSpeedKmh) * 3600);

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
     * Оценивает извилистость маршрута относительно прямой между началом и концом.
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
     * Вычисляет интегральную интенсивность поворотов маршрута.
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

        return totalTurn / totalDistanceKm;
    }

    /**
     * Формирует поправочный коэффициент сложности маршрута.
     */
    private double calculateCorrectionFactor(double curvature, double turnIntensity) {
        double alpha = 0.15;
        double beta = 0.2;

        double K = 1.0 + alpha * curvature + beta * turnIntensity;
        return Math.max(1.0, Math.min(K, 2.0));
    }
}
