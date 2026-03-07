package com.rws.api.rest.locks;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * REST DTO с данными шлюза для клиентов {@code rws-api}.
 */
@Getter
@Setter
@ToString
public class LockForRws {

    /**
     * Уникальный идентификатор шлюза в справочнике.
     */
    private String id;

    /**
     * Название шлюза.
     */
    private String name;

    /**
     * Географическая широта шлюза (WGS84).
     */
    private double latitude;

    /**
     * Географическая долгота шлюза (WGS84).
     */
    private double longitude;
}
