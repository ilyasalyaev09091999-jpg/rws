package com.archive.api.grpc.mapper;

import com.archive.api.business.read.dto.ArchiveTripSearchItem;
import com.archive.api.business.read.dto.ArchiveTripSearchResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Маппер результатов поиска архива между внутренними DTO и protobuf.
 */
@Component
public class ArchiveGrpcSearchMapper {

    /**
     * Маппит результат поиска в protobuf-ответ.
     *
     * @param source результат поиска
     * @return protobuf-ответ поиска
     */
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

    /**
     * Преобразует дату в строку {@code yyyy-MM-dd} для protobuf.
     *
     * @param value дата
     * @return строковое представление даты или пустая строка
     */
    private String toDateString(LocalDate value) {
        return value == null ? "" : value.toString();
    }

    /**
     * Преобразует десятичное число в строку для protobuf.
     *
     * @param value число
     * @return строковое представление числа или пустая строка
     */
    private String toDecimalString(BigDecimal value) {
        return value == null ? "" : value.toPlainString();
    }

    /**
     * Преобразует {@code null} в пустую строку.
     *
     * @param value исходное значение
     * @return пустая строка при {@code null}, иначе исходное значение
     */
    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}