package com.rws.api.rest.ports;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO для сервиса rws-api
 */
@Getter
@Setter
@ToString
public class PortForRws {

    /**
     * Уникальный ID порта
     */
    private String id;

    /**
     * Название порта
     */
    private String name;

    /**
     * Географическая широта
     */
    private double latitude;

    /**
     * Географическая долгота
     */
    private double longitude;
}
