package com.archive.api.business.read;

import com.archive.api.access_data.db.jdbc.repository.ArchiveRouteStatsRepository;
import com.archive.api.business.read.dto.ArchiveRouteStatsItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис получения агрегированной статистики по маршрутам архива.
 */
@Service
@RequiredArgsConstructor
public class ArchiveRouteStatsService {

    private final ArchiveRouteStatsRepository archiveRouteStatsRepository;

    /**
     * Возвращает статистику по маршрутам с учетом фильтров.
     *
     * @param departurePoint опциональная точка отправления
     * @param destinationPoint опциональная точка назначения
     * @param month опциональный месяц отправления (1..12)
     * @return список статистических элементов
     */
    public List<ArchiveRouteStatsItem> stats(String departurePoint, String destinationPoint, Integer month) {
        return archiveRouteStatsRepository.findStats(departurePoint, destinationPoint, month);
    }
}