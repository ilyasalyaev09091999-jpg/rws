package com.route.api.business.core.preparing;

import com.route.api.business.core.enitites.RouteNode;
import com.route.api.business.core.refdata.locks.LockDto;
import com.route.api.business.core.client.RouteFinderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Бизнес-компонент route-api: PrepareRouteResponse.
 */
@Service
@RequiredArgsConstructor
public class PrepareRouteResponse {

    /**
     * Формирует итоговый ответ API на основе рассчитанного маршрута.
     */
    public RouteFinderResponse prepare(List<RouteNode> route, LocalDateTime departureTime, List<LockDto> routeLocks,
                                       long timeRoute) {
        double totalDistanceKm = route.get(route.size() - 1).cost() / 1000.0;

        return new RouteFinderResponse(formatDuration(timeRoute), departureTime.plusSeconds(timeRoute), totalDistanceKm, route, routeLocks);
    }


    private static String formatDuration(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;

        if (hours > 0) {
            return String.format("%d С‡ %02d РјРёРЅ", hours, minutes);
        }
        return String.format("%d РјРёРЅ", minutes);
    }
}
