package com.geography.importer.access_data.db.jpa.repository;

import com.geography.importer.access_data.db.jpa.model.WayNodeEntity;
import com.geography.importer.access_data.db.jpa.model.WayNodeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA-репозиторий для таблицы связей {@code way_nodes}.
 */
@Repository
public interface WayNodeRepository extends JpaRepository<WayNodeEntity, WayNodeId> {
}
