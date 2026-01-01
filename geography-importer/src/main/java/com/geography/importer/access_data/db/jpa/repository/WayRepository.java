package com.geography.importer.access_data.db.jpa.repository;

import com.geography.importer.access_data.db.jpa.model.WayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WayRepository extends JpaRepository<WayEntity, Long> {
}
