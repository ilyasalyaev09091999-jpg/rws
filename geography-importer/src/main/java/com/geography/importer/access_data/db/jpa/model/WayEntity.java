package com.geography.importer.access_data.db.jpa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ways")
@Getter
@Setter
@NoArgsConstructor
public class WayEntity {

    @Id
    private Long id;

    @OneToMany(mappedBy = "way", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id.sequenceIndex ASC") // очень важно
    private List<WayNodeEntity> nodes = new ArrayList<>();
}
