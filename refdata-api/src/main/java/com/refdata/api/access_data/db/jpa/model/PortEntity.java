package com.refdata.api.access_data.db.jpa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Класс-модель порта для таблицы ports (справочная информация о портах).
 */
@Entity
@Table(name = "ports")
@Getter
@Setter
@NoArgsConstructor
public class PortEntity {

    /**
     * Уникальный ID порта
     */
    @Id
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
