package com.geography.importer.access_data.db.jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

/**
 * JPA-сущность ноды навигационного графа ({@code nodes}).
 */
@Entity
@Table(name = "nodes")
@Getter
@Setter
@NoArgsConstructor
public class NodeEntity {

    /**
     * Идентификатор OSM ноды.
     */
    @Id
    private Long id;

    /**
     * Долгота ноды.
     */
    @Column(nullable = false)
    private double longitude;

    /**
     * Широта ноды.
     */
    @Column(nullable = false)
    private double latitude;

    /**
     * Геометрия точки в PostGIS (Point, SRID 4326).
     */
    @JdbcTypeCode(SqlTypes.GEOMETRY)
    @Column(columnDefinition = "geometry(Point,4326)", nullable = false)
    private Point geom;
}
