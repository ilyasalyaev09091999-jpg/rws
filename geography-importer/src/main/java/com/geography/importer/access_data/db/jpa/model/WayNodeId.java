package com.geography.importer.access_data.db.jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Составной первичный ключ для таблицы {@code way_nodes}.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WayNodeId implements Serializable {

    /**
     * Идентификатор пути ({@code ways.id}).
     */
    @Column(name = "way_id")
    private Long wayId;

    /**
     * Позиция ноды внутри пути (начиная с 0).
     */
    @Column(name = "sequence_index")
    private Integer sequenceIndex;
}
