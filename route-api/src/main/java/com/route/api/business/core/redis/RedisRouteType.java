package com.route.api.business.core.redis;

import com.route.api.business.core.refdata.ports.PortDto;

import java.util.List;

/**
 * Перечисление стратегий/вариантов: RedisRouteType.
 */
public enum RedisRouteType {
    PORT_TO_PORT,
    AD_HOC;


    /**
     * Определяет тип маршрута для выбора стратегии кэширования.
     */
    public static RedisRouteType defineRouteType(List<PortDto> allPorts, double startLongitude, double startLatitude, double endLongitude,
                                                 double endLatitude) {
        if (allPorts == null || allPorts.isEmpty()) {
            return AD_HOC;
        }

        boolean isPortStart = false;
        boolean isPortEnd = false;
        for (PortDto port : allPorts) {

            if (port.latitude() == startLatitude && port.longitude() == startLongitude) {
                isPortStart = true;
            }

            if (port.latitude() == endLatitude && port.longitude() == endLongitude) {
                isPortEnd = true;
            }

            if (isPortStart && isPortEnd) {
                return PORT_TO_PORT;
            }
        }
        return AD_HOC;
    }
}
