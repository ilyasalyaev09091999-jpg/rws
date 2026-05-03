package com.archive.api.business.read;

import com.archive.api.access_data.db.jpa.repository.ArchiveTripJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Returns available archive points that can be used in search filters.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArchivePointSuggestionsService {

    private final ArchiveTripJpaRepository tripRepository;

    /**
     * Читает из архива полный distinct-список точек отправления и назначения.
     *
     * @return список городов/точек, доступных для подсказок в фильтрах
     */
    public List<String> getPoints() {
        log.info("Loading archive point suggestions from repository");
        List<String> points = tripRepository.findDistinctPoints();
        log.info("Archive point suggestions loaded. pointsCount={}", points.size());
        return points;
    }
}
