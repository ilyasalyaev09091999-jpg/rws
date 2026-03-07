package com.refdata.api.domain.entities.route_api;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO порта для {@code route-api}.
 * <p>
 * Содержит только координаты, необходимые для определения типа маршрута
 * и расчетов маршрутизации.
 * </p>
 */
@Getter
@Setter
@ToString
public class PortForRoute {

    /**
     * Географическая широта порта (WGS84).
     */
    private double latitude;

    /**
     * Географическая долгота порта (WGS84).
     */
    private double longitude;
}
