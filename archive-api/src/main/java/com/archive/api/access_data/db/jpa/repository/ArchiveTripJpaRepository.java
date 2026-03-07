package com.archive.api.access_data.db.jpa.repository;

import com.archive.api.access_data.db.jpa.model.ArchiveTripEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ArchiveTripJpaRepository extends JpaRepository<ArchiveTripEntity, Long> {

    boolean existsBySourceFileNameAndSourceRowNum(String sourceFileName, Integer sourceRowNum);

    @Query("""
            select t
            from ArchiveTripEntity t
            where lower(t.departurePoint) = lower(coalesce(:departurePoint, t.departurePoint))
              and lower(t.destinationPoint) = lower(coalesce(:destinationPoint, t.destinationPoint))
              and t.departureDate >= coalesce(:dateFrom, t.departureDate)
              and t.departureDate <= coalesce(:dateTo, t.departureDate)
            """)
    Page<ArchiveTripEntity> search(
            @Param("departurePoint") String departurePoint,
            @Param("destinationPoint") String destinationPoint,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            Pageable pageable);
}
