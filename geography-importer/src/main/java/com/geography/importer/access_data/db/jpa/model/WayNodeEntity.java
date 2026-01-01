package com.geography.importer.access_data.db.jpa.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "way_nodes")
@Data
public class WayNodeEntity {

    @EmbeddedId
    private WayNodeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("wayId")
    @JoinColumn(name = "way_id")
    private WayEntity way;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node_id", nullable = false)
    private NodeEntity node;
}
