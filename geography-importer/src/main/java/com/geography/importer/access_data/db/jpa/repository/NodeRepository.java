package com.geography.importer.access_data.db.jpa.repository;

import com.geography.importer.access_data.db.jpa.model.NodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA-репозиторий для таблицы {@code nodes}.
 */
@Repository
public interface NodeRepository extends JpaRepository<NodeEntity, Long> {
}
