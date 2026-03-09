package com.archive.api.grpc.handler;

import com.archive.api.business.read.ArchiveTripSearchService;
import com.archive.api.business.read.dto.ArchiveTripSearchItem;
import com.archive.api.business.read.dto.ArchiveTripSearchResponse;
import com.archive.api.util.DateParserUtils;
import com.archive.grpc.ArchiveSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ArchiveGrpcSearchHandler {

    private final ArchiveTripSearchService archiveTripSearchService;

    public com.archive.grpc.ArchiveTripSearchResponse handle(ArchiveSearchRequest request) {
        String departurePoint = nullIfBlank(request.getDeparturePoint());
        String destinationPoint = nullIfBlank(request.getDestinationPoint());
        LocalDate dateFrom = DateParserUtils.parseIsoDateOrNull(request.getDateFrom());
        LocalDate dateTo = DateParserUtils.parseIsoDateOrNull(request.getDateTo());

        ArchiveTripSearchResponse result = archiveTripSearchService.search(
                departurePoint,
                destinationPoint,
                dateFrom,
                dateTo,
                request.getPage(),
                request.getSize()
        );

        com.archive.grpc.ArchiveTripSearchResponse.Builder responseBuilder = com.archive.grpc.ArchiveTripSearchResponse.newBuilder()
                .setPage(result.page())
                .setSize(result.size())
                .setTotalElements(result.totalElements())
                .setTotalPages(result.totalPages());

        for (ArchiveTripSearchItem item : result.items()) {
            com.archive.grpc.ArchiveTripItem.Builder itemBuilder = com.archive.grpc.ArchiveTripItem.newBuilder()
                    .setVoyageName(nullToEmpty(item.voyageName()))
                    .setTripType(nullToEmpty(item.tripType()))
                    .setTugName(nullToEmpty(item.tugName()))
                    .setDeparturePoint(nullToEmpty(item.departurePoint()))
                    .setDestinationPoint(nullToEmpty(item.destinationPoint()))
                    .setDepartureDate(toDateString(item.departureDate()))
                    .setArrivalDate(toDateString(item.arrivalDate()))
                    .setCargoType(nullToEmpty(item.cargoType()))
                    .setCargoAmount(toDecimalString(item.cargoAmount()))
                    .setDraftM(toDecimalString(item.draftM()))
                    .setCounterpartyName(nullToEmpty(item.counterpartyName()))
                    .setCounterpartyInn(nullToEmpty(item.counterpartyInn()))
                    .setFlag(nullToEmpty(item.flag()))
                    .setRegionFrom(nullToEmpty(item.regionFrom()))
                    .setRegionTo(nullToEmpty(item.regionTo()));

            if (item.id() != null) {
                itemBuilder.setId(item.id());
            }
            if (item.durationDays() != null) {
                itemBuilder.setDurationDays(item.durationDays());
            }
            if (item.unitsCount() != null) {
                itemBuilder.setUnitsCount(item.unitsCount());
            }

            responseBuilder.addItems(itemBuilder.build());
        }

        return responseBuilder.build();
    }

    private String toDateString(LocalDate value) {
        return value == null ? "" : value.toString();
    }

    private String toDecimalString(BigDecimal value) {
        return value == null ? "" : value.toPlainString();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String nullIfBlank(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }
}
