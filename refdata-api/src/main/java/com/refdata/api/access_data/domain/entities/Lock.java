package com.refdata.api.access_data.domain.entities;

import jakarta.persistence.Column;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Универсальный класс "Шлюз" для бизнес логики и клиента
 */
@Getter
@Setter
@EqualsAndHashCode
public class Lock {

    /**
     * Уникальный идентификатор шлюза
     */
    @EqualsAndHashCode.Include
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

    /**
     * Привязка к node_id из таблицы nodes
     *
     */
    private long nodeId;

}
