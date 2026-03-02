package com.rws.api.rest.locks;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO для сервиса rws-api
 */
@Getter
@Setter
@ToString
public class LockForRws {

    /**
     * Уникальный идентификатор шлюза
     */
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
