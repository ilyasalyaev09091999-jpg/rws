package com.refdata.api.domain.entities.rws_api;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO для сервиса rws-api
 */
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class PortForRws {

    /**
     * Уникальный ID порта
     */
    @ToString.Include
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
