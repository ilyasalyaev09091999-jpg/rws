package com.refdata.api.access_data.domain.entities;

import lombok.Getter;
import lombok.Setter;

/**
 * Универсальный класс "Порт" для бизнес логики и клиента
 */
@Getter
@Setter
public class Port {

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
