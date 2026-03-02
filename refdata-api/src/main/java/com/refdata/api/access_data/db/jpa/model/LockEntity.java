package com.refdata.api.access_data.db.jpa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * Класс-модель для таблицы locks (справочная информация о шлюзах)
 */
@Entity
@Table(name = "locks")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class LockEntity {

    /**
     * Уникальный идентификатор шлюза
     */
    @Id
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

    /**
     * Узлы графа, принадлежащие шлюзу
     * (вход, выход, возможные альтернативные камеры)
     */
    @ElementCollection
    @CollectionTable(
            name = "lock_nodes",
            joinColumns = @JoinColumn(name = "lock_id")
    )
    @Column(name = "node_id")
    private Set<Long> nodeIds = new HashSet<>();
}
