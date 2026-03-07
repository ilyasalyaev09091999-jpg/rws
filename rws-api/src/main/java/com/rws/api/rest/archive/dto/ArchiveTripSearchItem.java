package com.rws.api.rest.archive.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ArchiveTripSearchItem(
        Long id,
        String voyageName,
        String tripType,
        String tugName,
        String departurePoint,
        String destinationPoint,
        LocalDate departureDate,
        LocalDate arrivalDate,
        Integer durationDays,
        String cargoType,
        BigDecimal cargoAmount,
        BigDecimal draftM,
        String counterpartyName,
        String counterpartyInn,
        String flag,
        Integer unitsCount,
        String regionFrom,
        String regionTo
) {
}
