package com.refdata.api.access_data.db.jpa.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * JPA-сущность таблицы {@code ports}.
 */
@Entity
@Table(name = "ports")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class PortEntity {

    /**
     * Первичный ключ порта.
     */
    @Id
    @ToString.Include
    private String id;

    /**
     * Название порта.
     */
    private String name;

    /**
     * Географическая широта (WGS84).
     */
    private double latitude;

    /**
     * Географическая долгота (WGS84).
     */
    private double longitude;
}
