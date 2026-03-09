package com.archive.api.grpc.mapper;

import com.archive.api.business.read.dto.ArchiveTripSearchItem;
import com.archive.api.business.read.dto.ArchiveTripSearchResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class ArchiveGrpcSearchMapper {

    public com.archive.grpc.ArchiveTripSearchResponse toProto(ArchiveTripSearchResponse source) {
        com.archive.grpc.ArchiveTripSearchResponse.Builder responseBuilder = com.archive.grpc.ArchiveTripSearchResponse.newBuilder()
                .setPage(source.page())
                .setSize(source.size())
                .setTotalElements(source.totalElements())
                .setTotalPages(source.totalPages());

        for (ArchiveTripSearchItem item : source.items()) {
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
}
