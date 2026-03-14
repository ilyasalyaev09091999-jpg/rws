package com.rws.api.rest.archive.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Элемент результата поиска архивных рейсов.
 *
 * @param id идентификатор рейса в архиве
 * @param voyageName название рейса/рейса-номера из источника
 * @param tripType тип рейса
 * @param tugName название буксира
 * @param departurePoint точка отправления (город/порт)
 * @param destinationPoint точка назначения (город/порт)
 * @param departureDate дата отправления
 * @param arrivalDate дата прибытия
 * @param durationDays длительность в днях (arrival - departure)
 * @param cargoType тип груза
 * @param cargoAmount количество груза
 * @param draftM осадка в метрах
 * @param counterpartyName название контрагента
 * @param counterpartyInn ИНН контрагента
 * @param flag флаг из источника
 * @param unitsCount количество единиц/секции
 * @param regionFrom регион отправления
 * @param regionTo регион назначения
 */
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