package com.refdata.api.domain.entities.rws_api;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO шлюза для gRPC-ответов в адрес {@code rws-api}.
 * <p>
 * Содержит пользовательские поля: идентификатор, название и координаты.
 * </p>
 */
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class LockForRws {

    /**
     * Уникальный идентификатор шлюза.
     */
    @ToString.Include
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
