package com.refdata.api.access_data.db.jpa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс-модель для таблицы directory_locks (справочная информация о шлюзах)
 */
@Entity
@Table(name = "locks")
@Getter
@Setter
public class LockEntity {

    /**
     * Уникальный идентификатор шлюза
     */
    @Id
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
