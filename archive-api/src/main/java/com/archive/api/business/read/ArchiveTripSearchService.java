package com.archive.api.business.read;

import com.archive.api.access_data.db.jpa.model.ArchiveTripEntity;
import com.archive.api.access_data.db.jpa.repository.ArchiveTripJpaRepository;
import com.archive.api.business.read.dto.ArchiveTripSearchItem;
import com.archive.api.business.read.dto.ArchiveTripSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArchiveTripSearchService {

    private final ArchiveTripJpaRepository tripRepository;

    public ArchiveTripSearchResponse search(String departurePoint,
                                            String destinationPoint,
                                            LocalDate dateFrom,
                                            LocalDate dateTo,
                                            int page,
                                            int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 200);

        PageRequest pageRequest = PageRequest.of(
                safePage,
                safeSize,
                Sort.by(Sort.Order.desc("departureDate"), Sort.Order.desc("id"))
        );

        Page<ArchiveTripEntity> result = tripRepository.search(departurePoint, destinationPoint, dateFrom, dateTo, pageRequest);
        List<ArchiveTripSearchItem> items = result.getContent().stream()
                .map(this::toItem)
                .toList();

        return new ArchiveTripSearchResponse(
                items,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    private ArchiveTripSearchItem toItem(ArchiveTripEntity e) {
        return new ArchiveTripSearchItem(
                e.getId(),
                e.getVoyageName(),
                e.getTripType(),
                e.getTugName(),
                e.getDeparturePoint(),
                e.getDestinationPoint(),
                e.getDepartureDate(),
                e.getArrivalDate(),
                e.getDurationDays(),
                e.getCargoType(),
                e.getCargoAmount(),
                e.getDraftM(),
                e.getCounterpartyName(),
                e.getCounterpartyInn(),
                e.getFlag(),
                e.getUnitsCount(),
                e.getRegionFrom(),
                e.getRegionTo()
        );
    }
}
