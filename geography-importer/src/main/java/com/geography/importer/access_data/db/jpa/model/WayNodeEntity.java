package com.geography.importer.access_data.db.jpa.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * JPA-сущность связи между путем и нодой ({@code way_nodes}).
 * <p>
 * Содержит составной ключ ({@link WayNodeId}) и ссылки на родительский
 * путь и соответствующую ноду.
 * </p>
 */
@Entity
@Table(name = "way_nodes")
@Data
public class WayNodeEntity {

    /**
     * Составной ключ: путь + позиция ноды в пути.
     */
    @EmbeddedId
    private WayNodeId id;

    /**
     * Путь, которому принадлежит нода.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("wayId")
    @JoinColumn(name = "way_id")
    private WayEntity way;

    /**
     * Ссылка на ноду графа.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node_id", nullable = false)
    private NodeEntity node;
}
