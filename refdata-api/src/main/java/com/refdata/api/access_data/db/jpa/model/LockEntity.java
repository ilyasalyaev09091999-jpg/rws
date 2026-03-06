package com.refdata.api.access_data.db.jpa.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * JPA-сущность таблицы {@code locks}.
 */
@Entity
@Table(name = "locks")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class LockEntity {

    /**
     * Первичный ключ шлюза.
     */
    @Id
    @ToString.Include
    private String id;

    /**
     * Название шлюза.
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

    /**
     * Идентификаторы узлов графа, связанных со шлюзом (таблица {@code lock_nodes}).
     */
    @ElementCollection
    @CollectionTable(
            name = "lock_nodes",
            joinColumns = @JoinColumn(name = "lock_id")
    )
    @Column(name = "node_id")
    private Set<Long> nodeIds = new HashSet<>();
}
