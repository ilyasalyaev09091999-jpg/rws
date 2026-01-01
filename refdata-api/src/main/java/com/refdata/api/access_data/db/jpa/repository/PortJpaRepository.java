package com.refdata.api.access_data.db.jpa.repository;

import com.refdata.api.access_data.db.jpa.model.PortEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA репозиторий для таблицы "порты"
 */
@Repository
public interface PortJpaRepository extends JpaRepository<PortEntity, String> {
}
