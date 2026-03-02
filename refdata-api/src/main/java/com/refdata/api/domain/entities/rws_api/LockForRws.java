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
public class LockForRws {

    /**
     * Уникальный идентификатор шлюза
     */
    @ToString.Include
    private String id;

    /**
     * Название (например, «Шлюз №15 Волго-Дона»)
     */
    private String name;

    /**
     * Широта
     */
    private double latitude;

    /**
     * Долгота
     */
    private double longitude;
}
