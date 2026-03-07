package com.refdata.api.domain.entities.rws_api;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO порта для gRPC-ответов в адрес {@code rws-api}.
 * <p>
 * Содержит внешний набор полей: идентификатор, название и координаты.
 * </p>
 */
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class PortForRws {

    /**
     * Уникальный идентификатор порта.
     */
    @ToString.Include
    private String id;

    /**
     * Человекочитаемое название порта.
     */
    private String name;

    /**
     * Географическая широта порта (WGS84).
     */
    private double latitude;

    /**
     * Географическая долгота порта (WGS84).
     */
    private double longitude;
}
