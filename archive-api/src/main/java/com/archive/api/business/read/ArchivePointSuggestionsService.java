package com.archive.api.business.read;

import com.archive.api.access_data.db.jpa.repository.ArchiveTripJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Returns available archive points that can be used in search filters.
 */
@Service
@RequiredArgsConstructor
public class ArchivePointSuggestionsService {

    private final ArchiveTripJpaRepository tripRepository;

    public List<String> getPoints() {
        return tripRepository.findDistinctPoints();
    }
}
