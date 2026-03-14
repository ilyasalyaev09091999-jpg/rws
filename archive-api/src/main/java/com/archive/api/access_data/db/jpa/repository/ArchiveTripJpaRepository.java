package com.archive.api.access_data.db.jpa.repository;

import com.archive.api.access_data.db.jpa.model.ArchiveTripEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

/**
 * JPA-репозиторий для чтения/записи записей архива рейсов.
 */
@Repository
public interface ArchiveTripJpaRepository extends JpaRepository<ArchiveTripEntity, Long> {

    /**
     * Проверяет, существует ли запись для указанного файла и номера строки.
     *
     * @param sourceFileName имя файла-источника
     * @param sourceRowNum номер строки в файле
     * @return {@code true}, если запись уже существует
     */
    boolean existsBySourceFileNameAndSourceRowNum(String sourceFileName, Integer sourceRowNum);

    /**
     * Выполняет поиск по фильтрам с поддержкой пагинации.
     *
     * @param departurePoint опциональная точка отправления
     * @param destinationPoint опциональная точка назначения
     * @param dateFrom опциональная нижняя граница даты отправления
     * @param dateTo опциональная верхняя граница даты отправления
     * @param pageable настройки пагинации
     * @return страница результатов
     */
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