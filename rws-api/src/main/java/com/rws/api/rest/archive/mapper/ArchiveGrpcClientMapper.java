package com.rws.api.rest.archive.mapper;

import com.archive.grpc.ArchiveAnalyticsRequest;
import com.archive.grpc.ArchiveImportJobStatusResponse;
import com.archive.grpc.ArchiveImportResultResponse;
import com.archive.grpc.ArchiveImportXlsxRequest;
import com.archive.grpc.ArchiveSearchRequest;
import com.rws.api.rest.archive.dto.ArchiveImportJobStatus;
import com.rws.api.rest.archive.dto.ArchiveImportResult;
import com.rws.api.rest.archive.dto.ArchiveRouteStatsItem;
import com.rws.api.rest.archive.dto.ArchiveTripSearchItem;
import com.rws.api.rest.archive.dto.ArchiveTripSearchResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Маппер между protobuf-контрактом {@code archive-proto} и REST DTO слоя {@code rws-api}.
 */
@Component
public class ArchiveGrpcClientMapper {

    /**
     * Формирует protobuf-запрос на импорт XLSX.
     *
     * @param fileName исходное имя файла (может быть {@code null})
     * @param content байты файла
     * @return protobuf-запрос с именем файла и содержимым
     */
    public ArchiveImportXlsxRequest toProtoImportRequest(String fileName, byte[] content) {
        return ArchiveImportXlsxRequest.newBuilder()
                .setFileName(fileName == null ? "unknown.xlsx" : fileName)
                .setContent(com.google.protobuf.ByteString.copyFrom(content))
                .build();
    }

    /**
     * Формирует protobuf-запрос поиска.
     *
     * @param departurePoint опциональная точка отправления (город/порт)
     * @param destinationPoint опциональная точка назначения (город/порт)
     * @param dateFrom опциональная нижняя граница даты отправления (включительно)
     * @param dateTo опциональная верхняя граница даты отправления (включительно)
     * @param page номер страницы (0-based)
     * @param size размер страницы
     * @return protobuf-запрос поиска
     */
    public ArchiveSearchRequest toProtoSearchRequest(String departurePoint,
                                                     String destinationPoint,
                                                     LocalDate dateFrom,
                                                     LocalDate dateTo,
                                                     int page,
                                                     int size) {
        return ArchiveSearchRequest.newBuilder()
                .setDeparturePoint(nullToEmpty(departurePoint))
                .setDestinationPoint(nullToEmpty(destinationPoint))
                .setDateFrom(dateFrom == null ? "" : dateFrom.toString())
                .setDateTo(dateTo == null ? "" : dateTo.toString())
                .setPage(page)
                .setSize(size)
                .build();
    }

    /**
     * Формирует protobuf-запрос аналитики.
     *
     * @param departurePoint опциональная точка отправления (город/порт)
     * @param destinationPoint опциональная точка назначения (город/порт)
     * @param month опциональный месяц (1..12) или {@code null}
     * @return protobuf-запрос аналитики
     */
    public ArchiveAnalyticsRequest toProtoAnalyticsRequest(String departurePoint,
                                                           String destinationPoint,
                                                           Integer month) {
        return ArchiveAnalyticsRequest.newBuilder()
                .setDeparturePoint(nullToEmpty(departurePoint))
                .setDestinationPoint(nullToEmpty(destinationPoint))
                .setMonth(month == null ? 0 : month)
                .build();
    }

    /**
     * Маппинг ответа синхронного импорта в REST DTO.
     *
     * @param response protobuf-ответ
     * @return REST DTO со счетчиками импорта
     */
    public ArchiveImportResult fromProto(ArchiveImportResultResponse response) {
        return new ArchiveImportResult(
                response.getFileName(),
                response.getTotalRows(),
                response.getImportedRows(),
                response.getSkippedRows(),
                response.getErrorRows()
        );
    }

    /**
     * Маппинг статуса асинхронного импорта в REST DTO.
     *
     * @param response protobuf-ответ
     * @return REST DTO со статусом и счетчиками
     */
    public ArchiveImportJobStatus fromProto(ArchiveImportJobStatusResponse response) {
        return new ArchiveImportJobStatus(
                response.getJobId(),
                response.getStatus(),
                emptyToNull(response.getFileName()),
                response.getTotalRows(),
                response.getImportedRows(),
                response.getSkippedRows(),
                response.getErrorRows(),
                emptyToNull(response.getErrorMessage())
        );
    }

    /**
     * Маппинг результата поиска в REST DTO.
     *
     * @param response protobuf-ответ
     * @return REST DTO с элементами и метаданными пагинации
     */
    public ArchiveTripSearchResponse fromProto(com.archive.grpc.ArchiveTripSearchResponse response) {
        List<ArchiveTripSearchItem> items = response.getItemsList().stream()
                .map(item -> new ArchiveTripSearchItem(
                        item.hasId() ? item.getId() : null,
                        emptyToNull(item.getVoyageName()),
                        emptyToNull(item.getTripType()),
                        emptyToNull(item.getTugName()),
                        emptyToNull(item.getDeparturePoint()),
                        emptyToNull(item.getDestinationPoint()),
                        parseDateOrNull(item.getDepartureDate()),
                        parseDateOrNull(item.getArrivalDate()),
                        item.hasDurationDays() ? item.getDurationDays() : null,
                        emptyToNull(item.getCargoType()),
                        parseDecimalOrNull(item.getCargoAmount()),
                        parseDecimalOrNull(item.getDraftM()),
                        emptyToNull(item.getCounterpartyName()),
                        emptyToNull(item.getCounterpartyInn()),
                        emptyToNull(item.getFlag()),
                        item.hasUnitsCount() ? item.getUnitsCount() : null,
                        emptyToNull(item.getRegionFrom()),
                        emptyToNull(item.getRegionTo())
                ))
                .toList();

        return new ArchiveTripSearchResponse(
                items,
                response.getPage(),
                response.getSize(),
                response.getTotalElements(),
                response.getTotalPages()
        );
    }

    /**
     * Маппинг результата аналитики в REST DTO.
     *
     * @param response protobuf-ответ
     * @return список статистических элементов по маршрутам
     */
    public List<ArchiveRouteStatsItem> fromProto(com.archive.grpc.ArchiveRouteStatsResponse response) {
        return response.getItemsList().stream()
                .map(item -> new ArchiveRouteStatsItem(
                        emptyToNull(item.getDeparturePoint()),
                        emptyToNull(item.getDestinationPoint()),
                        item.getDepartureMonth(),
                        item.getTripsCount(),
                        item.getMinDays(),
                        item.getMaxDays(),
                        parseDecimalOrNull(item.getAvgDays()),
                        parseDecimalOrNull(item.getP50Days()),
                        parseDecimalOrNull(item.getP80Days()),
                        parseDecimalOrNull(item.getUncertaintyDays())
                ))
                .toList();
    }

    /**
     * Преобразует {@code null} в пустую строку для protobuf-полей.
     *
     * @param value исходное значение
     * @return пустая строка при {@code null}, иначе исходное значение
     */
    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    /**
     * Преобразует пустые или пробельные строки в {@code null}.
     *
     * @param value исходное значение
     * @return {@code null}, если строка пустая/пробельная, иначе исходное значение
     */
    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    /**
     * Парсит дату формата {@code yyyy-MM-dd} или возвращает {@code null} при ошибке.
     *
     * @param value строковая дата из protobuf
     * @return {@link LocalDate} либо {@code null}, если строка пустая или некорректная
     */
    private LocalDate parseDateOrNull(String value) {
        String normalized = emptyToNull(value);
        if (normalized == null) {
            return null;
        }
        try {
            return LocalDate.parse(normalized);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    /**
     * Парсит десятичное число или возвращает {@code null} при ошибке.
     *
     * @param value строковое число из protobuf
     * @return {@link BigDecimal} либо {@code null}, если строка пустая или некорректная
     */
    private BigDecimal parseDecimalOrNull(String value) {
        String normalized = emptyToNull(value);
        if (normalized == null) {
            return null;
        }
        try {
            return new BigDecimal(normalized);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}