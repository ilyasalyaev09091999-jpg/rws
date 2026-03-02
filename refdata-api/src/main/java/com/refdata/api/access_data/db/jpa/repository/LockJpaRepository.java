package com.refdata.api.access_data.db.jpa.repository;

import com.refdata.api.access_data.db.jpa.model.LockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA репозиторий для таблицы "шлюзы"
 */
@Repository
public interface LockJpaRepository extends JpaRepository<LockEntity, String> {

    @Query("""
       select distinct l
       from LockEntity l
       left join fetch l.nodeIds
       """)
    List<LockEntity> findAllWithNodeIds();
}
