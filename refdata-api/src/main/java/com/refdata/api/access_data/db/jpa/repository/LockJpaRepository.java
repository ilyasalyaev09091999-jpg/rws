package com.refdata.api.access_data.db.jpa.repository;

import com.refdata.api.access_data.db.jpa.model.LockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA репозиторий для таблицы "шлюзы"
 */
@Repository
public interface LockJpaRepository extends JpaRepository<LockEntity, String> {
}
