package com.geography.importer.access_data.db.jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WayNodeId implements Serializable {

    @Column(name = "way_id")
    private Long wayId;

    @Column(name = "sequence_index")
    private Integer sequenceIndex;
}
