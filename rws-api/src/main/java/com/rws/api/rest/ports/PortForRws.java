package com.rws.api.rest.ports;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * REST DTO с данными порта для клиентов {@code rws-api}.
 * <p>
 * Используется как внешняя модель ответа и не содержит технических деталей
 * gRPC/хранения. Формируется на основе данных, полученных от {@code refdata-api}.
 * </p>
 */
@Getter
@Setter
@ToString
public class PortForRws {

    /**
     * Уникальный идентификатор порта в справочнике.
     */
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
