package com.archive.api.business.read;

import com.archive.api.access_data.db.jdbc.repository.ArchiveRouteStatsRepository;
import com.archive.api.business.read.dto.ArchiveRouteStatsItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArchiveRouteStatsService {

    private final ArchiveRouteStatsRepository archiveRouteStatsRepository;

    public List<ArchiveRouteStatsItem> stats(String departurePoint, String destinationPoint, Integer month) {
        return archiveRouteStatsRepository.findStats(departurePoint, destinationPoint, month);
    }
}
