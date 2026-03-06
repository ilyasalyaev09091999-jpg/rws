package com.geography.importer.access_data.db.jpa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA-сущность OSM пути ({@code ways}).
 * <p>
 * Содержит упорядоченный список связей с нодами через {@link WayNodeEntity}.
 * </p>
 */
@Entity
@Table(name = "ways")
@Getter
@Setter
@NoArgsConstructor
public class WayEntity {

    /**
     * Идентификатор OSM way.
     */
    @Id
    private Long id;

    /**
     * Упорядоченный список нод, входящих в путь.
     */
    @OneToMany(mappedBy = "way", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id.sequenceIndex ASC") // очень важно
    private List<WayNodeEntity> nodes = new ArrayList<>();
}
